package com.github.edgeLogger.utils;

import com.github.edgeLogger.plc.DataTimeEntry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MapCompare {
    public static Map<String, DataTimeEntry> isEquals(Map<String, DataTimeEntry> map1, Map<String, DataTimeEntry> map2) {
        return map1.entrySet().stream()
                // 过滤出在 map2 中不存在或值不等的条目
                .filter(entry -> {
                    String key = entry.getKey();
                    DataTimeEntry value1 = entry.getValue();
                    DataTimeEntry value2 = map2.get(key);

                    // 检查：map2 中无此键 或 值不相等
                    return !value1.equals(value2);
                })
                // 收集到 LinkedHashMap 保留顺序
                .collect(Collectors.toMap(
                        Map.Entry<String, DataTimeEntry>::getKey,
                        Map.Entry<String, DataTimeEntry>::getValue,
                        (oldVal, newVal) -> newVal,
                        LinkedHashMap<String, DataTimeEntry>::new
                ));
    }
}
