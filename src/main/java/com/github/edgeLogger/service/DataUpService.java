package com.github.edgeLogger.service;

import com.github.edgeLogger.config.YamlConfig;
import com.github.edgeLogger.mqtt.MqttClient;
import com.github.edgeLogger.mqtt.MqttPublisher;
import com.github.edgeLogger.mqtt.MqttSubscriber;
import com.github.edgeLogger.plc.DataWithMetadata;
import com.github.edgeLogger.plc.PlcReaderTask;
import com.github.edgeLogger.utils.JsonConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.edgeLogger.config.YamlConfig.blockingQueue;

public class DataUpService {
    public static final Logger logger = LoggerFactory.getLogger("DataUpService.class");

    // 基础服务客户端，如果想复用连接，就把Client传给Wrapper，如果不复用，如让各自新建，就不传
    private final MqttClient mqttClient;

    ExecutorService executor = Executors.newCachedThreadPool(); // 线程池

    public DataUpService() throws MqttException {
        // 尝试连接plc
        if (!connectPLC()) {
            throw new RuntimeException("PLC连接会话初始化失败");
        }
        logger.info("PLC会话模式：短连接");

        // MQTT客户端初始化
        mqttClient = new MqttClient();
        mqttClient.buildMqttSession(
                YamlConfig.mqttConfig.getBroker(),
                YamlConfig.mqttConfig.getClientId(),
                YamlConfig.mqttConfig.getUsername(),
                YamlConfig.mqttConfig.getPassword(),
                YamlConfig.mqttConfig.isCleanStart(),
                YamlConfig.mqttConfig.getKeepAlive(),
                YamlConfig.mqttConfig.getTimeout(),
                YamlConfig.mqttConfig.isAutoReconnect()
        );
        // 初始化订阅器, 并订阅主题
        new MqttSubscriber(mqttClient.getMqttSession()).subscribe(YamlConfig.mqttConfig.getSubTopic(), YamlConfig.mqttConfig.getSubQos());
    }

    public void startService() {
        // 生产者（采集）线程
        startCollect();
        // 消费者线程
        startProcessDataFromPlc();

        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("收到关闭信号，终止MQTT连接");
            try {
                mqttClient.disconnect();
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    // 生产者（采集）线程，每周期往缓冲区存一次
    public void startCollect() {
        long collectIntervalMs = YamlConfig.generalConfig.getCollectIntervalMs();
        // long keepAliveIntervalMs = YamlConfig.generalConfig.getKeepAliveIntervalMs();

        logger.info("开始轮询PLC");
        // 使用AtomicBoolean控制线程启停
        AtomicBoolean producerRunning = new AtomicBoolean(true);

        // PLC轮询线程
        Thread producerThread = new Thread(() -> {
            while (producerRunning.get()) {
                try {
                    long start = System.currentTimeMillis();
                    CountDownLatch latch = new CountDownLatch(YamlConfig.plcConfigs.size());
                    YamlConfig.plcConfigs.forEach(
                            (plcConfig) -> executor.submit(new PlcReaderTask(plcConfig, latch))
                    );
                    // 添加关闭钩子（安全终止线程）
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        executor.shutdownNow(); // 发送中断信号
                        logger.info("收到关闭信号，终止所有PLC连接");
                    }));
                    latch.await();
                    logger.info("所有PLC采集线程执行完毕，本次采集任务执行完毕");
                    Duration duration = Duration.ofMillis(collectIntervalMs - (System.currentTimeMillis() - start));
                    logger.info("预计{}:{}秒后开始下一次采集任务", duration.toMinutes(), duration.toSecondsPart());
                    TimeUnit.MILLISECONDS.sleep(collectIntervalMs - (System.currentTimeMillis() - start));
                } catch (InterruptedException e) {
                    logger.error("PLC轮询线程被中断", e);
                    producerRunning.set(false);
                } catch (Exception e) {
                    logger.error("PLC轮询异常：", e);
                }
            }
        });

        producerThread.start();
    }

    // 启动消费者线程
    public void startProcessDataFromPlc() {
        MqttPublisher mqttPublisher = new MqttPublisher(mqttClient.getMqttSession());
        // 使用AtomicBoolean控制线程启停
        AtomicBoolean consumerRunning = new AtomicBoolean(true);
        // 创建消费者线程（Lambda形式）
        Thread consumerThread = new Thread(() -> {
            while (consumerRunning.get()) {
                try {
                    DataWithMetadata dataWithMetadata = blockingQueue.take();
                    // 从缓冲区取出数据，阻塞直到有数据可用
                    long start = System.currentTimeMillis();
                    String packedData = packData(dataWithMetadata);
                    // 发给broker
                    mqttPublisher.publish(packedData.getBytes(), YamlConfig.mqttConfig.getPubTopic(), YamlConfig.mqttConfig.getPubQos());
                    long duration = System.currentTimeMillis() - start;
                    logger.info("[PlcId: {}] 封装和发布完成，耗时: {} 毫秒 Topic: {}", dataWithMetadata.metadata().get("plcID"), duration, YamlConfig.mqttConfig.getPubTopic());
                } catch (InterruptedException | JsonProcessingException e) {
                    logger.info("Consumer interrupted");
                    consumerRunning.set(false);
                }
            }
        });
        // 启动线程
        consumerThread.start();
    }


    /**
     * 将从PLC采集的数据包装成json
     *
     * @param input 输入
     * @return 封装格式如下:
     */
    public String packData(DataWithMetadata input) throws JsonProcessingException {
        if (input == null) {
            throw new IllegalArgumentException("Map cannot be null");
        }

        // 构建目标json数据结构
        Map<String, Object> jsonResult = JsonConverter.convert(input.data(), input.metadata(), YamlConfig.generalConfig.getGatewayId(), YamlConfig.generalConfig.getIp());

        // JSON序列化输出
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jsonResult);
    }

    private boolean connectPLC() {
        try {
            // LOGGER.error("PLC连接成功");
            return true;
        } catch (Exception e) {
            // LOGGER.error("PLC连接失败",e);
            return false;
        }
    }
}
