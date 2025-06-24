package com.github.edgeLogger.config;

public class TagAddressConfig {
    private String tagName;
    private String tagAddress;
    private String dataType;


    public String getTagAddress() {
        return tagAddress;
    }

    public String getDataType() {
        return dataType;
    }

    public void setTagAddress(String tagAddress) {
        this.tagAddress = tagAddress;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
