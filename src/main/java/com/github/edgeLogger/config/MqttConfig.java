package com.github.edgeLogger.config;

public class MqttConfig {
    // 客户端配置
    private String broker;
    private String clientId;

    // connect 参数
    private String username;
    private String password;
    private boolean cleanStart;
    private int keepAlive;
    private int timeout;
    private boolean autoReconnect;
    private String msgExample;

    // 订阅参数
    private String subTopic;
    private int subQos;

    // 发布参数
    private String pubTopic;
    private int pubQos;

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isCleanStart() {
        return cleanStart;
    }

    public void setCleanStart(boolean cleanStart) {
        this.cleanStart = cleanStart;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public String getMsgExample() {
        return msgExample;
    }

    public void setMsgExample(String msgExample) {
        this.msgExample = msgExample;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

    public int getSubQos() {
        return subQos;
    }

    public void setSubQos(int subQos) {
        this.subQos = subQos;
    }

    public String getPubTopic() {
        return pubTopic;
    }

    public void setPubTopic(String pubTopic) {
        this.pubTopic = pubTopic;
    }

    public int getPubQos() {
        return pubQos;
    }

    public void setPubQos(int pubQos) {
        this.pubQos = pubQos;
    }
}
