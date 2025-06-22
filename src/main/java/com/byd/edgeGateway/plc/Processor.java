package com.byd.edgeGateway.plc;

import com.byd.edgeGateway.config.PlcConfig;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Processor {
    public static final Logger logger = LoggerFactory.getLogger("Processor.class");

    public static Map<String, DataTimeEntry> readPlc(PlcConfig plcConfig) throws Exception {
//        String connectionString = "s7://172.18.79.7?remote-rack=0&remote-slot=1&controller-type=S7_1200" +
//                "&tcp.keep-alive=true&tcp.no-delay=false";
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
            logger.info("采集完成，耗时: {}", System.currentTimeMillis() - start);

            for (String tagName : response.getTagNames()) {
                if (response.getResponseCode(tagName) == PlcResponseCode.OK) {
                    int numValues = response.getNumberOfValues(tagName);
                    if (numValues == 1) {
                        // 目前的话都是一个标签一个值
                        logger.info("Value[{}]: {}", tagName, response.getObject(tagName));
                        String tagNameHead = tagName.split("->")[0];
                        // 封装数据
                        double[] doubles = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0, (Double) response.getObject(tagName),0,0,0};
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
            return registerValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("value-1", "%DB10.DBD92:REAL[1]");
        stringStringHashMap.put("value-2", "%DB11.DBD92:REAL[1]");
        stringStringHashMap.put("value-3", "%DB12.DBD92:REAL[1]");
        stringStringHashMap.put("value-4", "%DB13.DBD92:REAL[1]");
        stringStringHashMap.put("value-5", "%DB14.DBD92:REAL[1]");
        stringStringHashMap.put("value-6", "%DB15.DBD92:REAL[1]");
        stringStringHashMap.put("value-7", "%DB16.DBD92:REAL[1]");
        stringStringHashMap.put("value-8", "%DB17.DBD92:REAL[1]");

    }
}
