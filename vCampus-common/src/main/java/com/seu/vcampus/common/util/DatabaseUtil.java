package com.seu.vcampus.common.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil implements Serializable {
    private static final String DB_URL = "jdbc:ucanaccess://C:\\Users\\何祥裕\\Documents\\ShopDao.accdb";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    static {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            System.out.println("UCanAccess JDBC驱动加载成功");
        } catch (ClassNotFoundException e) {
            System.err.println("加载UCanAccess JDBC驱动失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load UCanAccess JDBC driver");
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            System.out.println("尝试连接到数据库: " + DB_URL);
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("数据库连接成功");
            return conn;
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("数据库连接已关闭");
            } catch (SQLException e) {
                System.err.println("关闭数据库连接时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}