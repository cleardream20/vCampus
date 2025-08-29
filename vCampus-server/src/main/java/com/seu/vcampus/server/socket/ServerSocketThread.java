package com.seu.vcampus.server.socket;


import com.seu.vcampus.server.controller.LibraryController;
import com.seu.vcampus.server.controller.UserController;
import com.seu.vcampus.common.util.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerSocketThread extends Thread {
    private static final int PORT = 8888;

    private Map<String, Object> controllers = new HashMap<>();

    public ServerSocketThread() {
        // 初始化控制器
        controllers.put("LIBRARY", new LibraryController());
        controllers.put("USER", new UserController());
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
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {

            while (true) {
                // 读取客户端消息
                Message request = (Message) ois.readObject();

                // 处理请求
                Message response = new Message();
                String typeGroup = request.getType().split("_")[0];

                Object controller = controllers.get(typeGroup);
                if (controller != null) {
                    if (controller instanceof LibraryController) {
                        response = ((LibraryController) controller).handleRequest(request);
                    } else if (controller instanceof UserController) {
                        // 用户认证相关操作
                    }
                } else {
                    response.setStatus(Message.STATUS_ERROR);
                    response.setData("未知的请求类型: " + request.getType());
                }

                // 发送响应
                oos.writeObject(response);
                oos.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("客户端断开连接: " + clientSocket.getRemoteSocketAddress());//异常处理
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}