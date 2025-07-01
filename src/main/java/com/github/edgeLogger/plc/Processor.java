package com.github.edgeLogger.plc;

import com.github.edgeLogger.config.PlcConfig;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Processor {
    public static final Logger logger = LoggerFactory.getLogger("Processor.class");

    public static Map<String, DataTimeEntry> readPlc(PlcConfig plcConfig) throws Exception {
        String connectionString = String.format("s7://%s?remote-rack=%d&remote-slot=%d&controller-type=S7_1200" +
                "&tcp.keep-alive=true&tcp.no-delay=false", plcConfig.getIp(), plcConfig.getRack(), plcConfig.getSlot());

        Map<String, DataTimeEntry> registerValue = new LinkedHashMap<>();

        long start = System.currentTimeMillis();
        try (PlcConnection plcConnection = PlcDriverManager.getDefault().getConnectionManager().getConnection(connectionString)) {
            if (!plcConnection.getMetadata().isReadSupported()) {
                logger.warn("当前连接不支持“读”请求。");
                return registerValue;
            }

            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
            plcConfig.getRegisterConfigs().forEach(
                    (registerConfig) -> {
                        registerConfig.getTagAddresses().forEach((tagAddressConfig) ->
                                builder.addTagAddress(registerConfig.getDeviceID() + "->" + tagAddressConfig.getTagName(), tagAddressConfig.getTagAddress()));
                    }
            );
            PlcReadRequest readRequest = builder.build();

            PlcReadResponse response = readRequest.execute().get();

            for (String tagName : response.getTagNames()) {
                if (response.getResponseCode(tagName) == PlcResponseCode.OK) {
                    int numValues = response.getNumberOfValues(tagName);
                    if (numValues == 1) {
                        // 目前的话都是一个标签一个值
                        logger.info("Value[{}]: {}", tagName, response.getDouble(tagName));
                        String tagNameHead = tagName.split("->")[0];
                        // 封装数据，目前一个块只有一个地址需要采集，所以可以这样，如果一个块有多个地址需要采集，就不可以这样封装（值会相互覆盖）。
                        double[] doubles = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                new BigDecimal(Double.toString(response.getDouble(tagName))).setScale(2, RoundingMode.HALF_UP).doubleValue(), 0, 0, 0};
                        DataTimeEntry value = new DataTimeEntry(doubles, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
                        registerValue.put(tagNameHead, value);
                    } else {
                        logger.info("Value[{}]:", tagName);
                        for (int i = 0; i < numValues; i++) {
                            System.out.println(" - " + response.getObject(tagName, i));
                        }
                    }
                } else {
                    logger.error("Error[{}]: {}", tagName, response.getResponseCode(tagName).name());
                }
            }
            logger.info("[PlcID: {}] 采集完成，耗时: {} 毫秒", plcConfig.getPlcID(), System.currentTimeMillis() - start);
            return registerValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 清理资源，执行到这里已自动释放资源
            logger.info("[{}] 关闭PLC连接: {}", Thread.currentThread().getName(), plcConfig.getIp());
        }
    }

    public static int writePlc() {

        return 1;
    }
}
