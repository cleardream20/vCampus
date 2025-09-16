package com.seu.vcampus.server.socket;

import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.server.controller.ShopController;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ShopController shopController;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.shopController = new ShopController();
    }

    @Override
    public void run() {
        try (
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream())
        ) {
            while (!clientSocket.isClosed()) {
                // 读取客户端消息
                Message request = (Message) input.readObject();
                System.out.println("收到请求: " + request.getType());

                // 根据消息类型路由到相应的控制器
                Message response = null;

                if (request.getType().startsWith("STORE_")) {
                    response = shopController.handleRequest(request);
                } else if (request.getType().startsWith("AUTH_")) {
                    // 处理认证请求
                    // response = authController.handleRequest(request);
                } else if (request.getType().startsWith("COURSE_")) {
                    // 处理课程请求
                    // response = courseController.handleRequest(request);
                } else {
                    response = new Message();
                    response.setType("ERROR");
                    response.setStatus(Message.STATUS_ERROR);
                    response.setData("未知请求类型: " + request.getType());
                }

                // 发送响应
                output.writeObject(response);
                output.flush();
                System.out.println("发送响应: " + response.getType());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("处理客户端请求失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("客户端连接已关闭");
            } catch (IOException e) {
                System.err.println("关闭客户端连接失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}