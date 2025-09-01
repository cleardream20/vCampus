package com.seu.vcampus.server;

import com.seu.vcampus.server.socket.ServerSocketHandler;

public class ServerMain {
    public static void main(String[] args) {
        // 初始化内存数据
        DataManager.initialize();

        // 启动Socket服务器
        ServerSocketHandler server = new ServerSocketHandler(8888);
        server.start();
        System.out.println("服务器已启动，监听端口: 8888");
    }
}