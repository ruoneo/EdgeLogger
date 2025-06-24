package com.github.edgeLogger.persistence;

import com.github.edgeLogger.plc.DataTimeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PowerDataInserter {
    public static final Logger LOGGER = LoggerFactory.getLogger("PowerDataInserter.class");

    // 数据库连接配置
    private static final String DB_URL = "jdbc:mysql://172.18.79.100:3306/powerdb";
    private static final String DB_USER = "collector";
    private static final String DB_PASSWORD = "collector";

    public static void putPowerData(Map<String, DataTimeEntry> input, boolean pubStatus) {
        // 使用try-with-resources确保资源关闭
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // SQL插入语句
            String sql = "INSERT INTO powerData ("
                    + "device_id, data_time, pub_status,"
                    + "voltage1, voltage2, voltage3, voltage4, voltage5, voltage6, "
                    + "current1, current2, current3, "
                    + "active_power1, active_power2, active_power3, active_power4, "
                    + "reactive_power1, reactive_power2, reactive_power3, reactive_power4, "
                    + "apparent_power, "
                    + "system_param1, system_param2, "
                    + "energy1, energy2, energy3, energy4"
                    + ") VALUES (?, ?, ?,"
                    + "?, ?, ?, ?, ?, ?, "
                    + "?, ?, ?, "
                    + "?, ?, ?, ?, "
                    + "?, ?, ?, ?, "
                    + "?, "
                    + "?, ?, "
                    + "?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Map.Entry<String, DataTimeEntry> entry : input.entrySet()) {
                    String deviceId = entry.getKey();
                    DataTimeEntry dataEntry = entry.getValue();
                    double[] values = dataEntry.getDoubles();
                    String dataTime = dataEntry.getTimestamp();

                    // 设置参数
                    pstmt.setString(1, deviceId);          // device_id
                    pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.parse(dataTime, DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))));          // data_time
                    pstmt.setBoolean(3, pubStatus);          // pub_status

                    // 电压 (6个)
                    for (int i = 0; i < 6; i++) {
                        pstmt.setDouble(4 + i, values[i]);
                    }

                    // 电流 (3个)
                    for (int i = 0; i < 3; i++) {
                        pstmt.setDouble(10 + i, values[6 + i]);
                    }

                    // 有功功率 (4个)
                    for (int i = 0; i < 4; i++) {
                        pstmt.setDouble(13 + i, values[9 + i]);
                    }

                    // 无功功率 (4个)
                    for (int i = 0; i < 4; i++) {
                        pstmt.setDouble(17 + i, values[13 + i]);
                    }

                    // 视在功率 (1个)
                    pstmt.setDouble(21, values[17]);        // apparent_power

                    // 系统参数 (2个)
                    pstmt.setDouble(22, values[18]);        // system_param1
                    pstmt.setDouble(23, values[19]);        // system_param2

                    // 电能 (4个)
                    for (int i = 0; i < 4; i++) {
                        pstmt.setDouble(24 + i, values[20 + i]);
                    }

                    // 执行插入
                    pstmt.executeUpdate();
                }
                LOGGER.info("成功插入 {} 条数据", input.size());
            }
        } catch (SQLException e) {
            LOGGER.debug("数据库操作错误: {}", e.getMessage());
        }
    }
}