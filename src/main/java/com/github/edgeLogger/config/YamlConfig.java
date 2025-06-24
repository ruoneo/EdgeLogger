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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YamlConfig {
    public static final Logger LOGGER = LoggerFactory.getLogger("YamlConfigLoader.class");

    private final Path configPath;
    public GeneralConfig generalConfig = new GeneralConfig();
    public MqttConfig mqttConfig = new MqttConfig();
    public final ArrayList<PlcConfig> plcConfigs = new ArrayList<>();
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
            Map<String, Object> generalConfigMap = (Map<String, Object>) configMap.get("general");
            this.readGeneralConfig(generalConfigMap);

            // 加载MTQQ配置
            Map<String, Object> mqttConfigMap = (Map<String, Object>) configMap.get("mqtt");
            this.readMqttConfig(mqttConfigMap);

            // 加载PLC配置
            ArrayList<Object> plcLst = (ArrayList<Object>) configMap.get("plc");
            this.readPlcConfig(plcLst);

            this.lastReloadTime = configPath.toFile().lastModified(); // 更新最后加载时间
        } catch (Exception e) {
            throw new RuntimeException("加载配置失败", e);
        }
    }

    private void readGeneralConfig(Map<String, Object> generalConfigMap) {
        // 加载基础配置
        this.generalConfig.setGatewayId((String) generalConfigMap.get("gatewayId"));
        this.generalConfig.setIp((String) generalConfigMap.get("ipAddress"));
        this.generalConfig.setAlias((String) generalConfigMap.get("alias"));
        // 加载采集策略配置
        Map<String, Object> pollingConfigMap = (Map<String, Object>) generalConfigMap.get("polling");
        this.readPollingConfig(pollingConfigMap);
        // 加载采集策略配置
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
            this.plcConfigs.add(plcConfig);
        }

    }

    private List<RegisterConfig> readRegisterConfigs(Object plc) {
        // 处理寄存器
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
        this.mqttConfig.setBroker((String) mqttConfigMap.get("broker"));
        this.mqttConfig.setClientId((String) mqttConfigMap.get("clientId"));
        this.mqttConfig.setQos((Integer) mqttConfigMap.get("qos"));
        // connect 配置
        this.mqttConfig.setUsername((String) mqttConfigMap.get("username"));
        this.mqttConfig.setPassword(mqttConfigMap.get("password").toString());
        this.mqttConfig.setCleanStart((Boolean) mqttConfigMap.get("cleanStart"));
        this.mqttConfig.setKeepAlive((Integer) mqttConfigMap.get("keepAlive"));
        this.mqttConfig.setTimeout((Integer) mqttConfigMap.get("timeout"));
        this.mqttConfig.setAutoReconnect((Boolean) mqttConfigMap.get("autoReconnect"));
        this.mqttConfig.setMsgExample((String) mqttConfigMap.get("msgExample"));
        // 订阅参数
        this.mqttConfig.setSubTopic((String) mqttConfigMap.get("subTopic"));
        this.mqttConfig.setSubQos((Integer) mqttConfigMap.get("subQos"));
        // 发布参数
        this.mqttConfig.setPubTopic((String) mqttConfigMap.get("pubTopic"));
        this.mqttConfig.setPubQos((Integer) mqttConfigMap.get("pubQos"));
    }

    private void readDbConfig(Map<String, Object> dbConfigMap) {
        this.generalConfig.setDb_url((String) dbConfigMap.get("url"));
        this.generalConfig.setDb_user((String) dbConfigMap.get("user"));
        this.generalConfig.setDb_password((String) dbConfigMap.get("password"));
    }

    private void readPollingConfig(Map<String, Object> pollingConfigMap) {
        this.generalConfig.setKeepAliveIntervalMs((int) pollingConfigMap.get("keepAliveIntervalMs"));
        this.generalConfig.setCollectIntervalMs((int) pollingConfigMap.get("collectIntervalMs"));
    }

    public static long extractNumber(String input) throws NumberFormatException {
        Pattern pattern = Pattern.compile("^\\d+");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String numberStr = matcher.group();
            return Long.parseLong(numberStr);
        } else {
            throw new NumberFormatException("字符串中没有数字部分。");
        }
    }

    public Path getConfigPath() {
        return configPath;
    }

    public void setHotReload(boolean flag) {
        // LOGGER.info("配置热重载：{}", flag);
    }

    public long getLastReloadTime() {
        return lastReloadTime;
    }

    public void setLastReloadTime(long lastReloadTime) {
        this.lastReloadTime = lastReloadTime;
    }

    public GeneralConfig getGeneralConfig() {
        return generalConfig;
    }

    public void setGeneralConfig(GeneralConfig generalConfig) {
        this.generalConfig = generalConfig;
    }

    public MqttConfig getMqttConfig() {
        return mqttConfig;
    }

    public void setMqttConfig(MqttConfig mqttConfig) {
        this.mqttConfig = mqttConfig;
    }

    public ArrayList<PlcConfig> getPlcConfigs() {
        return plcConfigs;
    }

}

