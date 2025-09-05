package com.seu.vcampus.server;

import com.seu.vcampus.server.controller.AdminController;
import com.seu.vcampus.server.controller.CourseController;
import com.seu.vcampus.server.socket.ServerSocketHandler;

public class ServerMain {
    public static void main(String[] args) {
        // 1. 初始化控制器
        AdminController adminController = new AdminController();
        CourseController courseController = new CourseController();

        // 2. 创建服务器并注册控制器
        ServerSocketHandler server = new ServerSocketHandler(8080);
        server.addController("ADMIN", adminController);
        server.addController("COURSE", courseController);

        // 3. 启动服务器
        server.start();
        System.out.println("服务器运行中，按Ctrl+C停止...");
    }
}