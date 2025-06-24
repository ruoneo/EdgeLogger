package com.github.edgeLogger.plc;

import java.util.Map;

/**
 * @param data     原始数据
 * @param metadata 元数据
 */
public record DataWithMetadata(Map<String, DataTimeEntry> data, Map<String, Object> metadata) {
}
