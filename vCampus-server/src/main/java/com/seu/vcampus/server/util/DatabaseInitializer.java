package com.seu.vcampus.server.util;

import java.sql.*;

public class DatabaseInitializer {
    // 相对路径
    private static final String DB_URL = "jdbc:ucanaccess://./database/vcampus.accdb";

//    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
//        DatabaseMetaData meta = conn.getMetaData();
//        ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"});
//        return rs.next();
//    }
//
//    public static void createTableIfNotExists(Connection conn, String tableName, String createSql)
//            throws SQLException {
//        if (!tableExists(conn, tableName)) {
//            try (Statement stmt = conn.createStatement()) {
//                stmt.execute(createSql);
//                System.out.println("表 '" + tableName + "' 创建成功");
//            }
//        } else {
//            System.out.println("表 '" + tableName + "' 已存在");
//        }
//    }

    public static void init() {
        System.out.println("Initialize database...");
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // 创建 User 表
            String createUserTable =
                    "CREATE TABLE User (" +
                            "  cid TEXT(20) NOT NULL PRIMARY KEY," +
                            "  password TEXT(100) NOT NULL," +
                            "  tsid TEXT(20)," +
                            "  name TEXT(50) NOT NULL," +
                            "  email TEXT(100)," +
                            "  phone TEXT(20)," +
                            "  role TEXT(2) NOT NULL" +
                            ")";

            stmt.executeUpdate(createUserTable);
            System.out.println("User 表创建成功！");

        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println("User 表已存在，跳过创建。");
            } else {
                System.err.println("创建 User 表失败：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}