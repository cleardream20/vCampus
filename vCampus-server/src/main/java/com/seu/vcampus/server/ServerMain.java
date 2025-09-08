package com.seu.vcampus.server;

import com.seu.vcampus.server.controller.CourseController;
import com.seu.vcampus.server.dao.CourseDaoImpl;
import com.seu.vcampus.server.socket.ServerSocketHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerMain {
    public static void main(String[] args) {
        // 显示服务器启动信息
        printServerBanner();

        // 1. 检查数据库连接
        checkDatabaseConnection();

        // 2. 初始化控制器
        CourseController courseController = new CourseController();
        System.out.println("控制器初始化完成");

        // 3. 创建服务器并注册控制器
        int port = 8888;
        ServerSocketHandler server = new ServerSocketHandler(port);
        server.addController("COURSE", courseController);
        System.out.println("控制器注册完成: COURSE");

        // 4. 启动服务器
        server.start();
        System.out.println("服务器已启动，监听端口: " + port);
        System.out.println("等待客户端连接...");
        System.out.println("按 Ctrl+C 停止服务器");

        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n服务器正在关闭...");
            System.out.println("释放资源中...");
            System.out.println("服务器已安全关闭");
        }));
    }

    private static void checkDatabaseConnection() {
        System.out.println("检查数据库连接...");
        try {
            // 测试数据库连接
            if (CourseDaoImpl.testConnection()) {
                System.out.println("数据库连接成功");
            } else {
                System.err.println("数据库连接失败，尝试初始化数据库...");
                initializeDatabase();

                // 再次测试连接
                if (CourseDaoImpl.testConnection()) {
                    System.out.println("数据库初始化后连接成功");
                } else {
                    System.err.println("数据库初始化后仍然连接失败");
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.err.println("数据库连接检查失败: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void initializeDatabase() {
        try {
            System.out.println("开始初始化数据库...");
            CourseDaoImpl.initializeDatabase();
            System.out.println("数据库初始化完成");
        } catch (Exception e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printServerBanner() {
        System.out.println("==============================================");
        System.out.println("    vCampus 校园管理系统 - 服务器端");
        System.out.println("    版本: 1.0");
        System.out.println("    开发团队: SEU 软件工程");
        System.out.println("    启动时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println("==============================================");
    }
}