package com.github.edgeLogger.service;

import com.github.edgeLogger.config.GeneralConfig;
import com.github.edgeLogger.config.MqttConfig;
import com.github.edgeLogger.mqtt.MqttPublisher;
import com.github.edgeLogger.plc.DataTimeEntry;
import com.github.edgeLogger.utils.JsonConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProcessDataFromPlc {
    public static final Logger LOGGER = LoggerFactory.getLogger("ProcessDataFromPlc.class");

    private final BlockingQueue<Map<String, DataTimeEntry>> blockingQueue;
    private final MqttPublisher mqttPublisher;
    private final MqttConfig mqttConfig;
    private final GeneralConfig gatewayConfig;
    private Map<String, Object> lastJsonResult;

    public ProcessDataFromPlc(MqttPublisher mqttPublisher, MqttConfig mqttConfig, BlockingQueue<Map<String, DataTimeEntry>> blockingQueue, GeneralConfig gatewayConfig) {
        this.mqttPublisher = mqttPublisher;
        this.mqttConfig = mqttConfig;
        this.blockingQueue = blockingQueue;
        this.gatewayConfig = gatewayConfig;
    }

    // 设置一个重复任务，实现消费者角色
    public void startProcess() {
        // 使用AtomicBoolean控制线程启停
        AtomicBoolean consumerRunning = new AtomicBoolean(true);
        // 创建消费者线程（Lambda形式）
        Thread consumerThread = new Thread(() -> {
            while (consumerRunning.get()) {
                try {
                    // 从缓冲区取出数据，阻塞直到有数据可用
                    Map<String, DataTimeEntry> stringDataTimeEntryMap = blockingQueue.take();
                    long start = System.currentTimeMillis();
                    String packedData = packData(stringDataTimeEntryMap);
                    // 发给broker
                    mqttPublisher.publish(packedData.getBytes(), mqttConfig.getPubTopic(), mqttConfig.getPubQos());
                    long duration = System.currentTimeMillis() - start;
                    LOGGER.info("封装和发布完成，耗时: {} 毫秒 Topic: {}", duration, mqttConfig.getPubTopic());
                } catch (InterruptedException | JsonProcessingException e) {
                    LOGGER.info("Consumer interrupted");
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
     * @param input
     * @return 封装格式如下:
     */
    public String packData(Map<String, DataTimeEntry> input) throws JsonProcessingException {
        if (input == null) {
            throw new IllegalArgumentException("Map cannot be null");
        }

        // 构建目标json数据结构
        Map<String, Object> jsonResult = JsonConverter.convert(input, gatewayConfig.getGatewayId(), gatewayConfig.getIp());

        // JSON序列化输出
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(jsonResult);
    }
}
