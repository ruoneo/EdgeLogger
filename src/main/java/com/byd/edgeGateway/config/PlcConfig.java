package com.byd.edgeGateway.config;

import java.util.List;

public class PlcConfig {
    private String ip;
    private int port;
    private int timeout;
    private int rack;
    private int slot;
    private String plcID;
    List<RegisterConfig> registerConfigs;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRack() {
        return rack;
    }

    public void setRack(int rack) {
        this.rack = rack;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public List<RegisterConfig> getRegisterConfigs() {
        return registerConfigs;
    }

    public void setRegisterConfigs(List<RegisterConfig> registerConfigs) {
        this.registerConfigs = registerConfigs;
    }

    public String getPlcID() {
        return plcID;
    }

    public void setPlcID(String plcID) {
        this.plcID = plcID;
    }
}

