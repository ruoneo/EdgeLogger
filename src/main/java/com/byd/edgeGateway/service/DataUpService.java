package com.byd.edgeGateway.service;

import com.byd.edgeGateway.config.*;
import com.byd.edgeGateway.mqtt.MqttClient;
import com.byd.edgeGateway.mqtt.MqttPublisher;
import com.byd.edgeGateway.persistence.PowerDataInserter;
import com.byd.edgeGateway.plc.DataTimeEntry;
import com.byd.edgeGateway.plc.PlcReaderTask;
import com.byd.edgeGateway.plc.S7Client;
import com.byd.edgeGateway.utils.MapCompare;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataUpService {
    public static final Logger LOGGER = LoggerFactory.getLogger("DataUpService.class");

    // 基础服务客户端，如果想复用连接，就把Client传给Wrapper，如果不复用，如让各自新建，就不传
    public S7Client plcClient;
    public final MqttClient mqttClient;

    private YamlConfig yamlConfig;

    ExecutorService executor = Executors.newCachedThreadPool(); // 线程池

    public DataUpService() throws MqttException {
        // 尝试连接plc
        if(!connectPLC()){
            throw new RuntimeException("PLC连接会话初始化失败");
        }
        LOGGER.info("PLC会话模式：短连接");

        // MQTT客户端初始化
        mqttClient = new MqttClient();
//        mqttClient.buildMqttSession(
//                mqttConfig.getBroker(),
//                mqttConfig.getClientId(),
//                mqttConfig.getUsername(),
//                mqttConfig.getPassword(),
//                mqttConfig.isCleanStart(),
//                mqttConfig.getKeepAlive(),
//                mqttConfig.getTimeout(),
//                mqttConfig.isAutoReconnect()
//        );
        LOGGER.info("MQTT连接会话初始化完成");

        // 初始化订阅器, 并订阅主题
//        MqttSubscriber mqttSubscriber = new MqttSubscriber(mqttClient.getMqttSession());
//        mqttSubscriber.subscribe(mqttConfig.getSubTopic(), mqttConfig.getSubQos());
//        LOGGER.info("订阅主题: {} Qos: {}", mqttConfig.getSubTopic(), mqttConfig.getSubQos());
    }

    public void startService() {
        // 生产者（采集）线程
        startCollect();
        // 消费者线程
        startProcessDataFromPlc();

        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 关闭钩子启动
            LOGGER.info("安全断开钩子已启动");
            try {
                plcClient.disconnect();
                mqttClient.disconnect();
            } catch (MqttException | IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    // 生产者（采集）线程，每周期往缓冲区存一次
    public void startCollect() {
        long collectIntervalMs = yamlConfig.generalConfig.getCollectIntervalMs();
        long keepAliveIntervalMs = yamlConfig.generalConfig.getKeepAliveIntervalMs();

        LOGGER.info("开始轮询PLC");
        Object connectionLock = new Object();
        // 使用AtomicBoolean控制线程启停
        AtomicBoolean producerRunning = new AtomicBoolean(true);

        // 数据采集线程
        Thread dataThread = new Thread(() -> {
            while (producerRunning.get()) {
                try {
                    yamlConfig.plcConfigs.forEach(
                            (plcConfig)-> executor.submit(new PlcReaderTask(plcConfig))
                    );
                    // 添加关闭钩子（安全终止线程）
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        LOGGER.info("收到关闭信号，终止所有PLC连接...");
                        executor.shutdownNow(); // 发送中断信号
                    }));

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
                    TimeUnit.MILLISECONDS.sleep(collectIntervalMs);
                } catch (InterruptedException e) {
                    LOGGER.error("采集线程被中断", e);
                    producerRunning.set(false);
                } catch (Exception e) {
                    LOGGER.error("数据采集异常：", e);
                }
            }
        });

        dataThread.start();
    }

    // 启动消费者线程
    public void startProcessDataFromPlc() {
        // 初始化发布器
        // Mqtt Wrapper 简化调用
        MqttPublisher mqttPublisher = new MqttPublisher(mqttClient.getMqttSession());
        ProcessDataFromPlc processDataFromPlc = new ProcessDataFromPlc(mqttPublisher, mqttConfig, blockingQueue, gatewayConfig);
        processDataFromPlc.startProcess();
    }


    private boolean connectPLC() {
        try {
//            LOGGER.error("PLC连接成功");
            return true;
        } catch (Exception e) {
//            LOGGER.error("PLC连接失败",e);
            return false;
        }
    }
}
