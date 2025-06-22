package com.byd.edgeGateway.mqtt;

import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttSubscriber {
    public static final Logger LOGGER = LoggerFactory.getLogger("MqttSubscriber.class");

    private final IMqttAsyncClient mqttSession;

    public MqttSubscriber(IMqttAsyncClient mqttSession) {
        this.mqttSession = mqttSession;
        this.mqttSession.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String topic, MqttMessage message) {
                handleMessage(topic, message);
            }

            public void connectComplete(boolean reconnect, String serverURI) {
                LOGGER.info("connected to: {}", serverURI);
            }

            public void disconnected(MqttDisconnectResponse disconnectResponse) {
                LOGGER.info("disconnected: {}", disconnectResponse.getReasonString());
            }

            public void deliveryComplete(IMqttToken token) {
                LOGGER.info("deliveryComplete: {}", token.isComplete());
            }

            public void mqttErrorOccurred(MqttException exception) {
                LOGGER.info("mqttErrorOccurred: {}", exception.getMessage());
            }

            public void authPacketArrived(int reasonCode, MqttProperties properties) {
                LOGGER.info("authPacketArrived");
            }
        });
    }

    public void subscribe(String topic, int subQos) throws MqttException {
        mqttSession.subscribe(topic, subQos);
    }

    private void handleMessage(String topic, MqttMessage message) {
        LOGGER.info("[处理消息] Topic: {} Payload: {}", topic, new String(message.getPayload()));
    }

    public IMqttAsyncClient getMqttClient() {
        return mqttSession;
    }
}
