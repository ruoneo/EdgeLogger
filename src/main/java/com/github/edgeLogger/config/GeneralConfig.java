package com.github.edgeLogger.config;

public class GeneralConfig {
    private String gatewayId;
    private String ip;
    private String alias;

    // 采集策略配置
    private int keepAliveIntervalMs;
    private int collectIntervalMs;

    // db配置
    private String db_url;
    private String db_user;
    private String db_password;

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getCollectIntervalMs() {
        return collectIntervalMs;
    }

    public void setCollectIntervalMs(int collectIntervalMs) {
        this.collectIntervalMs = collectIntervalMs;
    }

    public int getKeepAliveIntervalMs() {
        return keepAliveIntervalMs;
    }

    public void setKeepAliveIntervalMs(int keepAliveIntervalMs) {
        this.keepAliveIntervalMs = keepAliveIntervalMs;
    }

    public String getDb_url() {
        return db_url;
    }

    public void setDb_url(String db_url) {
        this.db_url = db_url;
    }

    public String getDb_password() {
        return db_password;
    }

    public void setDb_password(String db_password) {
        this.db_password = db_password;
    }

    public String getDb_user() {
        return db_user;
    }

    public void setDb_user(String db_user) {
        this.db_user = db_user;
    }
}