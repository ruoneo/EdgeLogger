package com.github.edgeLogger.mqtt;

import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class MqttClient {
    public static final Logger LOGGER = LoggerFactory.getLogger("MqttClient.class");
    private static IMqttAsyncClient mqttSession;
    private final MqttConnectionOptions options = new MqttConnectionOptions();
    private final ExecutorService messageProcessor = Executors.newSingleThreadExecutor();
    private final ExecutorService senderExecutor = Executors.newCachedThreadPool();

    public void buildMqttSession(String broker, String clientId, String username, String password, boolean cleanStart, int keepAlive, int timeout, boolean autoReconnect) throws MqttException {
        mqttSession = new MqttAsyncClient(broker, clientId);
        options.setUserName(username);
        options.setPassword(password.getBytes(StandardCharsets.UTF_8));
        options.setCleanStart(cleanStart);
        options.setKeepAliveInterval(keepAlive);// 心跳间隔，单位为秒
        options.setConnectionTimeout(timeout);// 连接超时时间，单位为秒
        options.setAutomaticReconnect(autoReconnect);// 是否自动重连

        mqttSession.connect(options).waitForCompletion();

        LOGGER.info("MQTT会话初始化完成 [Broker：{}，ClientID：{}]", broker, clientId);
    }

    public void disconnect() throws MqttException {
        messageProcessor.shutdown();
        senderExecutor.shutdown();
        mqttSession.disconnect();
        LOGGER.info("MQTT会话已安全断开");
    }

    public IMqttAsyncClient getMqttSession() {
        return mqttSession;
    }
}
