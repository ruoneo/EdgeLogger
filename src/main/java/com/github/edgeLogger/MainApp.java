package com.github.edgeLogger;

import com.github.edgeLogger.config.YamlConfig;
import com.github.edgeLogger.service.DataUpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp {
    public static final Logger logger = LoggerFactory.getLogger("MainApp.class");
    private static final String CONFIG_PATH = "config.yaml";

    public static void main(String[] args) {
        try {
            // 初始化配置加载器
            YamlConfig yamlConfig = new YamlConfig(CONFIG_PATH);
            logger.info("配置文件加载完成");
            // 开启热重载
            yamlConfig.setHotReload(false);

            // 服务初始化
            DataUpService dataService = new DataUpService();
            logger.info("数据上行服务初始化完成");
            dataService.startService();
            logger.info("数据上行服务已启动");
        } catch (Exception e) {
            logger.error("启动失败", e);
            System.exit(1);
        }
    }
}