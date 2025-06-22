package com.byd.edgeGateway.config;

import java.util.ArrayList;
import java.util.List;

public class RegisterConfig {
    private int deviceNumber;
    private String deviceID;
    private String alias;
    private List<TagAddressConfig> tagAddresses;

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(int deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public List<TagAddressConfig> getTagAddresses() {
        return tagAddresses;
    }

    public void setTagAddresses(List<TagAddressConfig> tagAddresses) {
        this.tagAddresses = tagAddresses;
    }
}
