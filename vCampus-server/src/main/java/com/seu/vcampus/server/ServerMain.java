package com.seu.vcampus.server;

import com.seu.vcampus.server.controller.AdminController;
import com.seu.vcampus.server.controller.CourseController;
import com.seu.vcampus.server.socket.ServerSocketHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerMain {
    public static void main(String[] args) {
        // 显示服务器启动信息
        printServerBanner();

        // 1. 初始化控制器
        AdminController adminController = new AdminController();
        CourseController courseController = new CourseController();
        System.out.println("控制器初始化完成");

        // 2. 创建服务器并注册控制器
        int port = 8888;
        ServerSocketHandler server = new ServerSocketHandler(port);
        server.addController("ADMIN", adminController);
        server.addController("COURSE", courseController);
        System.out.println("控制器注册完成: ADMIN, COURSE");

        // 3. 启动服务器
        server.start();
        System.out.println("服务器已启动，监听端口: " + port);
        System.out.println("等待客户端连接...");
        System.out.println("按 Ctrl+C 停止服务器");

        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n服务器正在关闭...");
            System.out.println("释放资源中...");
            // 这里可以添加资源释放代码
            System.out.println("服务器已安全关闭");
        }));
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