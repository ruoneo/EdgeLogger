package com.github.edgeLogger.plc;

import com.github.edgeLogger.config.PlcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// PLC写任务（每个配置独立运行）
public class PlcWriterTask implements Runnable {
    public static final Logger logger = LoggerFactory.getLogger("PlcReader.class");

    private final PlcConfig plcConfig;

    public PlcWriterTask(PlcConfig plcConfig) {
        this.plcConfig = plcConfig;
    }

    @Override
    public void run() {
//        final String threadName = Thread.currentThread().getName();
//        logger.info("[{}] 开始读取PLC: {}:{}", threadName, plcConfig.getIp(), plcConfig.getPort());
//        try {
//            Map<String, DataTimeEntry> registerValue = Processor.readPlc(plcConfig);
//            // 一个PLC采集完成，值放入
//            Map<String, DataTimeEntry> registerValueDiff = MapCompare.isEquals(registerValue, plcConfig.getLastRegisterValue());
//            MapDifference<String, DataTimeEntry> mapDifference = Maps.difference(registerValueDiff, registerValue);
//            PowerDataInserter.putPowerData(mapDifference.entriesOnlyOnRight(),false);
//            if (registerValueDiff.isEmpty()) {
//                logger.info("PlcID:{} 数据无变化，跳过队列存储",plcConfig.getPlcID());
//            } else {
//                // 把registerValue写入数据库
//                PowerDataInserter.putPowerData(registerValueDiff,true);
////                blockingQueue.put(registerValueDiff);
//                plcConfig.setLastRegisterValue(registerValue);
//                logger.info("PlcID:{} 差异数据已存入队列",plcConfig.getPlcID());
//            }
//        } catch (Exception e) {
//            logger.info("[{}] PLC读取被中断", threadName);
//            throw new RuntimeException(e);
//        } finally {
//            // 清理资源（实际需要关闭PLC连接）
//            logger.info("[{}] 关闭PLC连接: {}", threadName, plcConfig.getIp());
//        }
    }
}
