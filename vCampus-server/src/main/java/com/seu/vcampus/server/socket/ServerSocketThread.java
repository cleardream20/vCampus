package com.seu.vcampus.server.socket;

import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.server.controller.LibraryController;
import com.seu.vcampus.server.controller.UserController;
import com.seu.vcampus.server.controller.ShopController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerSocketThread extends Thread {
    private static final int PORT = 8888;
    private static final int MAX_THREADS = 50; // 最大线程数

    private Map<String, Object> controllers = new HashMap<>();
    private ExecutorService threadPool;
    private boolean running = true;
    private ServerSocket serverSocket;

    public ServerSocketThread() {
        // 初始化线程池
        this.threadPool = Executors.newFixedThreadPool(MAX_THREADS);

        // 初始化控制器
        controllers.put("LIBRARY", new LibraryController());
        controllers.put("USER", new UserController());
        controllers.put("SHOP", new ShopController()); // 添加ShopController
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("服务端监听端口: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("客户端连接: " + clientSocket.getRemoteSocketAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("服务端异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            // 注意：先创建输出流，再创建输入流
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.flush(); // 刷新输出流头
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());

            while (running && !clientSocket.isClosed()) {
                try {
                    // 读取客户端消息
                    Message request = (Message) ois.readObject();
                    System.out.println("收到请求: " + request.getType());

                    // 处理请求
                    Message response = processRequest(request);
                    System.out.println("准备发送响应: " + response.getType());

                    // 发送响应
                    oos.writeObject(response);
                    oos.flush();
                    System.out.println("发送响应: " + response.getType());
                } catch (IOException e) {
                    System.out.println("客户端断开连接: " + clientSocket.getRemoteSocketAddress());
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("读取对象失败: " + e.getMessage());
                    e.printStackTrace();

                    // 发送错误响应
                    Message errorResponse = new Message();
                    errorResponse.setType("ERROR");
                    errorResponse.setStatus(Message.STATUS_ERROR);
                    errorResponse.setData("无效的请求格式");

                    oos.writeObject(errorResponse);
                    oos.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("处理客户端连接时发生错误: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("关闭客户端连接时发生错误: " + e.getMessage());
            }
        }
    }

    private Message processRequest(Message request) {
        Message response = new Message();
        response.setType(request.getType() + "_RESPONSE");

        try {
            String typeGroup = request.getType().split("_")[0];

            Object controller = controllers.get(typeGroup);
            if (controller != null) {
                if (controller instanceof LibraryController) {
                    response = ((LibraryController) controller).handleRequest(request);
                } else if (controller instanceof UserController) {
                    // 用户认证相关操作
                    // response = ((UserController) controller).handleRequest(request);
                } else if (controller instanceof ShopController) {
                    response = ((ShopController) controller).handleRequest(request);
                } else {
                    response.setStatus(Message.STATUS_ERROR);
                    response.setData("未实现的控制器: " + typeGroup);
                    System.err.println("未实现的控制器: " + typeGroup);
                }
            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("未知的请求类型: " + request.getType());
                System.err.println("未知的请求类型: " + request.getType());
            }
        } catch (Exception e) {
            System.err.println("处理请求时发生错误: " + e.getMessage());
            response.setStatus(Message.STATUS_ERROR);
            response.setData("处理请求时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        // 添加调试信息
        System.out.println("响应状态: " + response.getStatus());
        if (response.getData() != null) {
            System.out.println("响应数据类型: " + response.getData().getClass().getName());
            if (response.getData() instanceof List) {
                System.out.println("响应数据大小: " + ((List<?>) response.getData()).size());
            }
        } else {
            System.out.println("响应数据为null");
        }

        return response;
    }

    public void stopServer() {
        running = false;
        closeServer();
    }

    private void closeServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            // 关闭线程池
            if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.shutdown();
            }

            System.out.println("服务器已关闭");
        } catch (IOException e) {
            System.err.println("关闭服务器时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}