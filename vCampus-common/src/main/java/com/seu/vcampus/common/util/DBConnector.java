package com.seu.vcampus.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    // Access 数据库文件路径（相对路径或绝对路径）
    private static final String DB_PATH = "database/vCampus.accdb";

    // UCanAccess 连接 URL
    private static final String CONNECTION_URL =
            "jdbc:ucanaccess://./" + DB_PATH +
                    ";memory=false" +
                    ";keepMirror=true" +
                    ";openExclusive=false";

    /**
     * 获取数据库连接
     * @return Connection 对象
     * @throws SQLException 如果连接失败
     */
    public static Connection getConnection() throws SQLException {
        try {
            // 加载 UCanAccess 驱动（可选，DriverManager 会自动发现）
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("UCanAccess 驱动未找到，请检查依赖！", e);
        }

        return DriverManager.getConnection(CONNECTION_URL);
    }

    // 测试连接
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("成功通过 UCanAccess 连接到 Access 数据库！");
                System.out.println("数据库路径: " + new java.io.File(DB_PATH).getAbsolutePath());
            }
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}