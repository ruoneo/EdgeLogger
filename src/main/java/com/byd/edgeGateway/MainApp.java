package com.byd.edgeGateway;

import com.byd.edgeGateway.config.YamlConfig;
import com.byd.edgeGateway.service.DataUpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp {
    public static final Logger LOGGER = LoggerFactory.getLogger("MainApp.class");
//    private static final String CONFIG_PATH = "../../config/config.yaml";
    private static final String CONFIG_PATH = "config/config.yaml";

    public static void main(String[] args) {
        try {
            // 初始化配置加载器
            YamlConfig yamlConfigLoader = new YamlConfig(CONFIG_PATH);
            LOGGER.info("配置文件加载完成");
            // 开启热重载
            yamlConfigLoader.setHotReload(false);

            // 服务初始化
            DataUpService dataService = new DataUpService();
            LOGGER.info("数据上行服务初始化完成");
            dataService.startService();
            LOGGER.info("数据上行服务已启动");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}