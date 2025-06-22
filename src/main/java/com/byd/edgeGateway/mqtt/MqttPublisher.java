package com.byd.edgeGateway.mqtt;

import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttPublisher {
    public static final Logger LOGGER = LoggerFactory.getLogger("MqttPublisher.class");
    private final IMqttAsyncClient mqttSession;

    public MqttPublisher(IMqttAsyncClient mqttSession) {
        this.mqttSession = mqttSession;
    }

    public void publish(byte[] payload, String topic, int pubQos) {
        try {
            MqttMessage message = new MqttMessage(payload);
            message.setQos(pubQos);
            mqttSession.publish(topic, message);
        } catch (MqttException e) {
            LOGGER.debug("[Publish failure]: {}", e.getMessage());
        }
    }
}
