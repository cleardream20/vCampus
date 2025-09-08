package com.seu.vcampus.server.socket;

import com.seu.vcampus.common.util.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务器 Socket 监听线程
 * 负责监听端口，接收客户端连接
 */
public class ServerSocketThread extends Thread {

    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService clientExecutor = Executors.newCachedThreadPool();
    private volatile boolean running = true;

    public ServerSocketThread(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.printf("服务器监听端口: %d\n", port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.printf("客户端连接: %s:%d\n",
                        clientSocket.getInetAddress().getHostAddress(),
                        clientSocket.getPort());

                clientExecutor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("服务器异常: " + e.getMessage());
            } else {
                System.out.println("服务器已正常关闭");
            }
        }
    }

    /**
     * 关闭服务器（优雅关闭）
     */
    public void shutdown() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("关闭失败: " + e.getMessage());
        }
        clientExecutor.shutdown();
    }
}