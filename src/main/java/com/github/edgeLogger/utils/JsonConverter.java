package com.github.edgeLogger.utils;

import com.github.edgeLogger.plc.DataTimeEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class JsonConverter {
    public static Map<String, Object> convert(Map<String, DataTimeEntry> stringDataEntryMap, Map<String, Object> metadata, String gatewayId, String ipAddress) {
        Map<String, Object> root = new LinkedHashMap<>();

        // 固定头部信息
        root.put("msgType", "up");
        root.put("msgId", UUID.randomUUID().toString());
        root.put("msgCreateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));

        // 构建固定源信息
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("ipAddress", ipAddress);
        source.put("gatewayId", gatewayId);
        source.put("plc", Map.of("plcID", String.valueOf(metadata.get("plcID")), "ip", String.valueOf(metadata.get("ip"))));

        root.put("source", source);

        // 构建动态batch数据
        List<Map<String, Object>> batch = stringDataEntryMap.entrySet().stream()
                .map(entry -> buildBatchItem(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        root.put("batch", batch);
        return root;
    }

    private static Map<String, Object> buildBatchItem(String deviceId, DataTimeEntry data) {
        if (data.doubles.length < 24) {
            throw new IllegalArgumentException("数据项需要至少24个元素");
        }

        Map<String, Object> item = new LinkedHashMap<>();

        item.put("timestamp", data.timestamp); // 时间戳可按需修改
        item.put("device_type", "three_phase_meter");
        item.put("device_id", deviceId);
        item.put("metrics", buildMetrics(data.doubles));
        item.put("status", buildStatus());

        return item;
    }

    private static Map<String, Object> buildMetrics(double[] data) {
        Map<String, Object> metrics = new LinkedHashMap<>();

        // 电压
        metrics.put("voltage", buildVoltage(
                data[0], data[1], data[2],
                data[3], data[4], data[5]));

        // 电流
        metrics.put("current", buildCurrent(
                data[6], data[7], data[8]));

        // 功率
        metrics.put("power", buildPower(
                Arrays.copyOfRange(data, 9, 18))); // 截取9-17索引数据
        // 系统参数
        metrics.put("system", Arrays.asList(
                buildSystemParam("", "power_factor", data[18]),
                buildSystemParam("Hz", "frequency", data[19])
        ));

        // 电能
        metrics.put("energy", buildEnergy(
                data[20], data[21],
                data[22], data[23]));

        return metrics;
    }

    private static Map<String, Object> buildVoltage(
            double a, double b, double c, double ab, double bc, double ca) {
        Map<String, Object> voltage = new LinkedHashMap<>();
        voltage.put("data_type", "double");
        voltage.put("unit", "V");

        Map<String, Double> values = new LinkedHashMap<>();
        values.put("phase_a", a);
        values.put("phase_b", b);
        values.put("phase_c", c);
        values.put("line_ab", ab);
        values.put("line_bc", bc);
        values.put("line_ca", ca);
        voltage.put("value", values);

        return voltage;
    }

    private static Map<String, Object> buildCurrent(double a, double b, double c) {
        Map<String, Object> current = new LinkedHashMap<>();
        current.put("data_type", "double");
        current.put("unit", "A");

        Map<String, Double> values = new LinkedHashMap<>();
        values.put("phase_a", a);
        values.put("phase_b", b);
        values.put("phase_c", c);
        current.put("value", values);

        return current;
    }

    private static Map<String, Object> buildPower(double[] data) {
        Map<String, Object> power = new LinkedHashMap<>();

        // 有功功率
        power.put("active", buildPowerComponent(
                "kW", data[0], data[1], data[2], data[3]));

        // 无功功率
        power.put("reactive", buildPowerComponent(
                "kvar", data[4], data[5], data[6], data[7]));

        // 视在功率
        Map<String, Object> apparent = new LinkedHashMap<>();
        apparent.put("data_type", "double");
        apparent.put("unit", "kVA");
        Map<String, Double> apparentVal = new LinkedHashMap<>();
        apparentVal.put("total", data[8]);
        apparent.put("value", apparentVal);
        power.put("apparent", apparent);

        return power;
    }

    private static Map<String, Object> buildPowerComponent(
            String unit, double a, double b, double c, double total) {
        Map<String, Object> component = new LinkedHashMap<>();
        component.put("data_type", "double");
        component.put("unit", unit);

        Map<String, Double> values = new LinkedHashMap<>();
        values.put("phase_a", a);
        values.put("phase_b", b);
        values.put("phase_c", c);
        values.put("total", total);
        component.put("value", values);

        return component;
    }

    private static Map<String, Object> buildEnergy(
            double activeForward, double activeReverse,
            double reactiveForward, double reactiveReverse) {
        Map<String, Object> energy = new LinkedHashMap<>();

        // 有功电能
        energy.put("active", buildEnergyComponent("kW", activeForward, activeReverse));

        // 无功电能
        energy.put("reactive", buildEnergyComponent("kvarh", reactiveForward, reactiveReverse));

        return energy;
    }

    private static Map<String, Object> buildEnergyComponent(
            String unit, double forward, double reverse) {
        Map<String, Object> component = new LinkedHashMap<>();
        component.put("data_type", "double");
        component.put("unit", unit);

        Map<String, Double> values = new LinkedHashMap<>();
        values.put("forward", forward);
        values.put("reverse", reverse);
        component.put("value", values);

        return component;
    }

    private static Map<String, Object> buildSystemParam(
            String unit, String key, double value) {
        Map<String, Object> param = new LinkedHashMap<>();
        param.put("data_type", "double");
        param.put("unit", unit);
        param.put(key, value);
        return param;
    }

    private static Map<String, Integer> buildStatus() {
        Map<String, Integer> status = new LinkedHashMap<>();
        status.put("device_status", 0);
        status.put("data_quality", 100);
        return status;
    }
}
