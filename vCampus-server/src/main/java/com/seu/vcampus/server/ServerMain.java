package com.seu.vcampus.server;


import com.seu.vcampus.server.socket.ServerSocketThread;

public class ServerMain {
    public static void main(String[] args) {
        ServerSocketThread server = new ServerSocketThread();
        server.start();
        System.out.println("图书馆服务端已启动...");
    }
}