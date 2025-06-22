package com.byd.edgeGateway.plc;

import com.byd.edgeGateway.config.GeneralConfig;
import com.byd.edgeGateway.config.PlcConfig;
import com.byd.edgeGateway.config.RegisterConfig;
import com.byd.edgeGateway.persistence.PowerDataInserter;
import com.byd.edgeGateway.service.Callback;
import com.byd.edgeGateway.utils.MapCompare;
import com.github.s7connector.impl.serializer.converter.RealConverter;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

// PlcReader.java
public class PlcReader1 {
    public static final Logger LOGGER = LoggerFactory.getLogger("PlcReader.class");
    //real实数浮点...
    private static final RealConverter realCon = new RealConverter();

    private S7Client plcClient;
    private final PlcConfig plcConfig;
    private final BlockingQueue<Map<String, DataTimeEntry>> blockingQueue;

    // 进来默认已连接
    private volatile boolean isConnected = true;

    private Map<String, DataTimeEntry> lastRegisterValue = Collections.emptyMap();

    private Callback callback;

    private int temp = 0;

    public PlcReader1(S7Client plcClient, ArrayList<PlcConfig> plcConfig, BlockingQueue<Map<String, DataTimeEntry>> blockingQueue) {
        this.plcClient = plcClient;
        this.plcConfig = null;
        this.blockingQueue = blockingQueue;
    }

    public void startPolling(GeneralConfig gatewayConfig) {
        long collectIntervalMs = gatewayConfig.getCollectIntervalMs();
        long heartbeatIntervalMs = gatewayConfig.getKeepAliveIntervalMs();

        LOGGER.info("开始轮询PLC");
        Object connectionLock = new Object();
        // 使用AtomicBoolean控制线程启停
        AtomicBoolean producerRunning = new AtomicBoolean(true);
        // 心跳检测线程（5秒间隔）
        Thread heartbeatThread = new Thread(() -> {
            while (producerRunning.get()) {
                try {
                    synchronized (connectionLock) {
                        if (!checkHeartbeat()) {
                            LOGGER.warn("心跳丢失，尝试重新连接...");
                            if (!reconnectPLC()) {
                                LOGGER.error("连接无法恢复，暂停数据采集");
                                isConnected = false;
                                // 记录详细错误日志
                                LOGGER.error("PLC连接失败详情：", new Exception("Connection Lost"));
                            } else {
                                isConnected = true;
                                LOGGER.info("连接恢复成功");
                            }
                        }else{
                            LOGGER.info("心跳信号正常");
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(heartbeatIntervalMs);
                } catch (InterruptedException e) {
                    LOGGER.error("心跳线程被中断");
                    producerRunning.set(false);
                }
            }
        });

        // 数据采集线程（30秒间隔）
        Thread dataThread = new Thread(() -> {
            while (producerRunning.get()) {
                try {
                    synchronized (connectionLock) {
                        if (isConnected) {
                            long start = System.currentTimeMillis();
                            List<RegisterConfig> registers = plcConfig.getRegisterConfigs();
                            Map<String, DataTimeEntry> registerValue = plcClient.readBatch(registers);
                            bytesToFloat(registerValue);
                            LOGGER.info("采集完成，耗时: {} 毫秒", System.currentTimeMillis() - start);

                            Map<String, DataTimeEntry> registerValueDiff = MapCompare.isEquals(registerValue, lastRegisterValue);
                            MapDifference<String, DataTimeEntry> mapDifference = Maps.difference(registerValueDiff, registerValue);
                            PowerDataInserter.putPowerData(mapDifference.entriesOnlyOnRight(),false);
                            if (registerValueDiff.isEmpty()) {
                                LOGGER.info("数据无变化，跳过队列存储");
                            } else {
                                // 把registerValue写入数据库
                                PowerDataInserter.putPowerData(registerValueDiff,true);
                                blockingQueue.put(registerValueDiff);
                                lastRegisterValue = registerValue;
                                LOGGER.info("差异数据已存入队列");
                            }
                        }
                    }
                    TimeUnit.MILLISECONDS.sleep(collectIntervalMs);
                } catch (InterruptedException e) {
                    LOGGER.error("采集线程被中断", e);
                    producerRunning.set(false);
                } catch (Exception e) {
                    LOGGER.error("数据采集异常：", e);
                    synchronized (connectionLock) {
                        isConnected = false;
                    }
                }
            }
        });

        heartbeatThread.start();
        dataThread.start();
    }

    private void bytesToFloat(Map<String, DataTimeEntry> registerValue) {
        for (Map.Entry<String, DataTimeEntry> entry : registerValue.entrySet()) {
            String key = entry.getKey();
            byte[] bytes = entry.getValue().bytes;

            // 判断bytes是否为4的倍数, 如果不是抛出异常
            if (bytes.length % 4 != 0) {
                throw new IllegalArgumentException("bytesList中的每个元素长度必须为4的倍数");
            }
            // 包装字节数组到ByteBuffer
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            // 可选：设置字节顺序（默认BIG_ENDIAN）
            // buffer.order(ByteOrder.LITTLE_ENDIAN);

            double[] doubles = new double[bytes.length / 4];

            // 每4字节解析为一个float
            int index = 0;
            while (buffer.remaining() >= 4) {
                doubles[index] = buffer.getFloat();
                index++;
            }

            entry.getValue().doubles = doubles;
//            for (int i = 0; i < (bytes.length) / 4; i++) {
//                byte[] floatBytes = new byte[4];
//                // 读取四个字节
//                System.arraycopy(bytes, i * 4, floatBytes, 0, 4);
//                Double plcData = realCon.extract(Double.class, floatBytes, 0, 0);
//            }
        }
    }

    //心跳检测方法
    private boolean checkHeartbeat() {
        // 实现具体的PLC心跳发送逻辑
        // 例如：plcClient.sendHeartbeat();
        if (temp < 1) {
            temp++;
            return plcClient.readPlcDataReal(10, 4, 0);
        } else {
            return true;
        }
    }

    // 重连PLC
    private boolean reconnectPLC() {
        return callback.reconnectPLC();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setPlcClient(S7Client plcClient) {
        this.plcClient = plcClient;
    }
}