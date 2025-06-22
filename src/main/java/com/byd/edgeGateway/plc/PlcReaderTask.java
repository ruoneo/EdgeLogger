package com.byd.edgeGateway.plc;

import com.byd.edgeGateway.config.PlcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

// PLC读取任务（每个配置独立运行）
public class PlcReaderTask implements Runnable {
    public static final Logger logger = LoggerFactory.getLogger("PlcReader.class");

    private final PlcConfig plcConfig;

    public PlcReaderTask(PlcConfig plcConfig) {
        this.plcConfig = plcConfig;
    }

    @Override
    public void run() {
        final String threadName = Thread.currentThread().getName();
        logger.info("[%s] 开始读取PLC: %s:%d", threadName, plcConfig.getIp(), plcConfig.getPort());
        try {
            Map<String, DataTimeEntry> stringDataTimeEntryMap = Processor.readPlc(plcConfig);
        } catch (Exception e) {
            logger.info("[%s] PLC读取被中断", threadName);
            throw new RuntimeException(e);
        } finally {
            // 清理资源（实际需要关闭PLC连接）
            logger.info("[%s] 关闭PLC连接: %s", threadName, plcConfig.getIp());
        }
    }
}
