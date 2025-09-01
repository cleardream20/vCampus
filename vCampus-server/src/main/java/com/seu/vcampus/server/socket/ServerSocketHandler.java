package com.seu.vcampus.server.socket;

import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.server.controller.CourseController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSocketHandler extends Thread {
    private final int port;
    private final ExecutorService threadPool;
    private ServerSocket serverSocket;
    private boolean running = true;

    public ServerSocketHandler(int port) {
        this.port = port;
        this.threadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("服务器已启动，监听端口: " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("客户端连接: " + clientSocket.getRemoteSocketAddress());

                // 为每个客户端创建处理线程
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("服务器异常: " + e.getMessage());
            }
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        running = false;
        threadPool.shutdown();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("关闭服务器失败: " + e.getMessage());
        }
        System.out.println("服务器已关闭");
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final CourseController courseController;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.courseController = new CourseController();
        }

        @Override
        public void run() {
            try (
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())
            ) {
                while (!clientSocket.isClosed()) {
                    // 读取客户端消息
                    Message request = (Message) in.readObject();
                    System.out.println("收到请求: " + request.getType() + " from " + request.getSender());

                    // 处理请求
                    Message response = courseController.processRequest(request);

                    // 发送响应
                    out.writeObject(response);
                    out.flush();
                    System.out.println("发送响应: " + response.getType() + " to " + request.getSender());
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("客户端连接异常: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("关闭客户端连接失败: " + e.getMessage());
                }
            }
        }
    }
}