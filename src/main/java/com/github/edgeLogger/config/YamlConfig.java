package com.github.edgeLogger.config;

import com.github.edgeLogger.plc.DataWithMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class YamlConfig {
    public static final Logger logger = LoggerFactory.getLogger("YamlConfigLoader.class");

    private final Path configPath;
    public static GeneralConfig generalConfig = new GeneralConfig();
    public static MqttConfig mqttConfig = new MqttConfig();
    public static final ArrayList<PlcConfig> plcConfigs = new ArrayList<>();
    // 初始化缓冲区，容量为50
    public static final BlockingQueue<DataWithMetadata> blockingQueue = new ArrayBlockingQueue<>(50);

    private long lastReloadTime = 0;

    public YamlConfig(String filePath) {
        this.configPath = Path.of(filePath);
        reload(); // 初始加载
    }

    public void reload() {
        try (InputStream inputStream = new FileInputStream(String.valueOf(configPath))) {
            Yaml yaml = new Yaml();
            Map<String, Object> configMap = yaml.load(inputStream);

            // 加载generalConfig
            @SuppressWarnings("unchecked")
            Map<String, Object> generalConfigMap = (Map<String, Object>) configMap.get("general");
            this.readGeneralConfig(generalConfigMap);

            // 加载MTQQ配置
            @SuppressWarnings("unchecked")
            Map<String, Object> mqttConfigMap = (Map<String, Object>) configMap.get("mqtt");
            this.readMqttConfig(mqttConfigMap);

            // 加载PLC配置
            @SuppressWarnings("unchecked")
            ArrayList<Object> plcLst = (ArrayList<Object>) configMap.get("plc");
            this.readPlcConfig(plcLst);

            this.lastReloadTime = configPath.toFile().lastModified(); // 更新最后加载时间
        } catch (Exception e) {
            throw new RuntimeException("加载配置失败", e);
        }
    }

    public static GeneralConfig getGeneralConfig(){
        return YamlConfig.generalConfig;
    }

    private void readGeneralConfig(Map<String, Object> generalConfigMap) {
        // 加载基础配置
        YamlConfig.generalConfig.setGatewayId((String) generalConfigMap.get("gatewayId"));
        YamlConfig.generalConfig.setIp((String) generalConfigMap.get("ipAddress"));
        YamlConfig.generalConfig.setAlias((String) generalConfigMap.get("alias"));
        // 加载采集策略配置
        @SuppressWarnings("unchecked")
        Map<String, Object> pollingConfigMap = (Map<String, Object>) generalConfigMap.get("polling");
        this.readPollingConfig(pollingConfigMap);
        // 加载采集策略配置
        @SuppressWarnings("unchecked")
        Map<String, Object> dbConfigMap = (Map<String, Object>) generalConfigMap.get("db");
        this.readDbConfig(dbConfigMap);
    }

    private void readPlcConfig(ArrayList<Object> plcLst) {
        for (Object plc : plcLst) {
            PlcConfig plcConfig = new PlcConfig();
            plcConfig.setIp((String) ((LinkedHashMap<?, ?>) plc).get("ip"));
            plcConfig.setPort((Integer) ((LinkedHashMap<?, ?>) plc).get("port"));
            plcConfig.setTimeout((Integer) ((LinkedHashMap<?, ?>) plc).get("timeout"));
            plcConfig.setRack((Integer) ((LinkedHashMap<?, ?>) plc).get("rack"));
            plcConfig.setSlot((Integer) ((LinkedHashMap<?, ?>) plc).get("slot"));
            plcConfig.setPlcID((String) ((LinkedHashMap<?, ?>) plc).get("plcID"));
            plcConfig.setRegisterConfigs(readRegisterConfigs(plc));
            YamlConfig.plcConfigs.add(plcConfig);
        }

    }

    private List<RegisterConfig> readRegisterConfigs(Object plc) {
        // 处理寄存器
        @SuppressWarnings("unchecked")
        ArrayList<Object> registers = (ArrayList<Object>) ((LinkedHashMap<?, ?>) plc).get("registers");
        ArrayList<RegisterConfig> registerConfigs = new ArrayList<>();
        for (Object register : registers) {
            RegisterConfig registerConfig = new RegisterConfig();
            registerConfig.setDeviceNumber((int) ((LinkedHashMap<?, ?>) register).get("deviceNumber"));
            registerConfig.setDeviceID((String) ((LinkedHashMap<?, ?>) register).get("deviceId"));
            registerConfig.setAlias((String) ((LinkedHashMap<?, ?>) register).get("alias"));
            registerConfig.setTagAddresses(readTagAddressConfigs(register));
            registerConfigs.add(registerConfig);
        }
        return registerConfigs;
    }

    private List<TagAddressConfig> readTagAddressConfigs(Object register) {
        @SuppressWarnings("unchecked")
        ArrayList<Object> tagAddresses = (ArrayList<Object>) ((LinkedHashMap<?, ?>) register).get("tagAddresses");
        ArrayList<TagAddressConfig> tagAddressConfigs = new ArrayList<>();
        for (Object tagAddress : tagAddresses) {
            TagAddressConfig tagAddressConfig = new TagAddressConfig();
            tagAddressConfig.setTagName((String) ((LinkedHashMap<?, ?>) tagAddress).get("tagName"));
            tagAddressConfig.setTagAddress((String) ((LinkedHashMap<?, ?>) tagAddress).get("tagAddress"));
            tagAddressConfig.setDataType((String) ((LinkedHashMap<?, ?>) tagAddress).get("dataType"));
            tagAddressConfigs.add(tagAddressConfig);
        }
        return tagAddressConfigs;
    }

    private void readMqttConfig(Map<String, Object> mqttConfigMap) {
        // MTQQ客户端配置
        YamlConfig.mqttConfig.setBroker((String) mqttConfigMap.get("broker"));
        YamlConfig.mqttConfig.setClientId((String) mqttConfigMap.get("clientId"));
        // connect 配置
        YamlConfig.mqttConfig.setUsername((String) mqttConfigMap.get("username"));
        YamlConfig.mqttConfig.setPassword(mqttConfigMap.get("password").toString());
        YamlConfig.mqttConfig.setCleanStart((Boolean) mqttConfigMap.get("cleanStart"));
        YamlConfig.mqttConfig.setKeepAlive((Integer) mqttConfigMap.get("keepAlive"));
        YamlConfig.mqttConfig.setTimeout((Integer) mqttConfigMap.get("timeout"));
        YamlConfig.mqttConfig.setAutoReconnect((Boolean) mqttConfigMap.get("autoReconnect"));
        YamlConfig.mqttConfig.setMsgExample((String) mqttConfigMap.get("msgExample"));
        // 订阅参数
        YamlConfig.mqttConfig.setSubTopic((String) mqttConfigMap.get("subTopic"));
        YamlConfig.mqttConfig.setSubQos((Integer) mqttConfigMap.get("subQos"));
        // 发布参数
        YamlConfig.mqttConfig.setPubTopic((String) mqttConfigMap.get("pubTopic"));
        YamlConfig.mqttConfig.setPubQos((Integer) mqttConfigMap.get("pubQos"));
    }

    private void readDbConfig(Map<String, Object> dbConfigMap) {
        YamlConfig.generalConfig.setDb_url((String) dbConfigMap.get("url"));
        YamlConfig.generalConfig.setDb_name((String) dbConfigMap.get("dbName"));
        YamlConfig.generalConfig.setDb_table((String) dbConfigMap.get("dbTable"));
        YamlConfig.generalConfig.setDb_user((String) dbConfigMap.get("user"));
        YamlConfig.generalConfig.setDb_password((String) dbConfigMap.get("password"));
    }

    private void readPollingConfig(Map<String, Object> pollingConfigMap) {
        YamlConfig.generalConfig.setKeepAliveIntervalMs((int) pollingConfigMap.get("keepAliveIntervalMs"));
        YamlConfig.generalConfig.setCollectIntervalMs((int) pollingConfigMap.get("collectIntervalMs"));
    }

    public Path getConfigPath() {
        return configPath;
    }

    public void setHotReload(boolean flag) {
        logger.info("配置热重载：{}", flag);
    }

    public long getLastReloadTime() {
        return lastReloadTime;
    }
}

