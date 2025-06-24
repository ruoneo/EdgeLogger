package com.github.edgeLogger.persistence;

public class BufferManager {
//    private final ArrayBlockingQueue<byte[]> buffer;
//    private final Connection connection;
//
//    public DataStorageService(SqliteConfig config) {
//        this.buffer = new ArrayBlockingQueue<>(config.getBufferSize());
//        try {
//            Class.forName("org.sqlite.JDBC");
//            this.connection = DriverManager.getConnection("jdbc:sqlite:" + config.getPath());
//            initializeDatabase();
//            startPersistentWorker();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to initialize SQLite", e);
//        }
//    }
//
//    private void initializeDatabase() throws Exception {
//        try (PreparedStatement stmt = connection.prepareStatement(
//                "CREATE TABLE IF NOT EXISTS plc_data (" +
//                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                        "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
//                        "data BLOB)")) {
//            stmt.executeUpdate();
//        }
//    }
//
//    public void bufferData(byte[] data) {
//        buffer.offer(data);
//    }
//
//    private void startPersistentWorker() {
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
//            try {
//                byte[] data = buffer.poll(1, TimeUnit.SECONDS);
//                if (data != null) {
//                    try (PreparedStatement stmt = connection.prepareStatement(
//                            "INSERT INTO plc_data (data) VALUES (?)")) {
//                        stmt.setBytes(1, data);
//                        stmt.executeUpdate();
//                    }
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            } catch (Exception e) {
//                System.err.println("Persistent worker error: " + e.getMessage());
//            }
//        }, 0, 500, TimeUnit.MILLISECONDS);
//    }
}
