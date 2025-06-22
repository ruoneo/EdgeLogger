package com.byd.edgeGateway.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Instant;

public class DataStorage {
    public static final Logger log = LoggerFactory.getLogger("DataStorage.class");

    private Connection connection;
    private final String dbPath;

    public DataStorage(String dbPath) {
        this.dbPath = dbPath;
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS raw_data (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "timestamp BIGINT NOT NULL," +
                        "device_id TEXT NOT NULL," +
                        "data_type TEXT NOT NULL," +
                        "value BLOB NOT NULL)");
                stmt.execute("CREATE INDEX IF NOT EXISTS idx_timestamp ON raw_data(timestamp)");
            }
            log.info("成功初始化数据库连接: {}", dbPath);
        } catch (SQLException e) {
            log.error("数据库初始化失败: {}", e.getMessage());
            throw new RuntimeException("数据库连接异常", e);
        }
    }

    public void insertData(String deviceId, String dataType, byte[] value) {
        String sql = "INSERT INTO raw_data(timestamp, device_id, data_type, value) VALUES(?,?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, Instant.now().toEpochMilli());
            pstmt.setString(2, deviceId);
            pstmt.setString(3, dataType);
            pstmt.setBytes(4, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("数据插入失败: {}", e.getMessage());
            throw new RuntimeException("数据库操作异常", e);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("数据库连接已关闭");
            }
        } catch (SQLException e) {
            log.warn("数据库关闭异常: {}", e.getMessage());
        }
    }
}
