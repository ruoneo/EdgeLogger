package com.github.edgeLogger.config;

import java.nio.file.*;

public class ConfigWatcher implements Runnable {
    private final YamlConfig yamlConfigLoader;

    public ConfigWatcher(YamlConfig yamlConfigLoader) {
        this.yamlConfigLoader = yamlConfigLoader;
    }

    @Override
    public void run() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path dir = yamlConfigLoader.getConfigPath().getParent();
            dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().equals(yamlConfigLoader.getConfigPath().getFileName().toString())) {
                        long currentModified = yamlConfigLoader.getConfigPath().toFile().lastModified();
                        if (currentModified > yamlConfigLoader.getLastReloadTime()) {
                            yamlConfigLoader.reload();
                            System.out.println("配置已热更新！");
                        }
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
