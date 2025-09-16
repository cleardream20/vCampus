// 确保ServerMain已经启动
package com.seu.vcampus.server;

import com.seu.vcampus.server.socket.ServerSocketThread;

public class ServerMain {
    public static void main(String[] args) {
        ServerSocketThread server = new ServerSocketThread();
        server.start();
        System.out.println("虚拟校园系统服务端已启动，监听端口: 8888");
    }
}