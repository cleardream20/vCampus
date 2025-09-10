package com.seu.vcampus.server.socket;

import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.server.controller.UserController;

import java.io.*;
import java.net.Socket;

/**
 * 处理单个客户端请求的线程
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final UserController userController;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.userController = new UserController(); // 可改为依赖注入
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)
        ) {
            String line;
            StringBuilder jsonBuffer = new StringBuilder();

            while ((line = in.readLine()) != null) {
                jsonBuffer.append(line);
                // 简单协议：JSON 以 } 结尾
                if (line.trim().endsWith("}")) {
                    break;
                }
            }

            String requestJson = jsonBuffer.toString().trim();
            if (requestJson.isEmpty()) return;

            // 解析请求
            Message request;
            try {
                request = Message.fromJson(requestJson);
            } catch (Exception e) {
                out.println(Message.fromData(Message.RESPONSE, false, null, "无效的 JSON 格式").toJson());
                return;
            }

            // 路由到控制器
            Message response = routeRequest(request);

            // 发送响应
            out.println(response.toJson());

        } catch (IOException e) {
            System.err.println("客户端通信错误: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("客户端连接错误: " +  e.getMessage());
            }
            System.out.printf("客户端断开: %s:%d\n",
                    socket.getInetAddress().getHostAddress(),
                    socket.getPort());
        }
    }

    private Message routeRequest(Message request) {
        String type = request.getType();

        System.out.println("==== 请求类型==== : " + type);

        // TODO: 可以用 Map<String, Controller> 优化
        switch (type) {
            case Message.LOGIN:
                try {
                    return userController.handleRequest(request);
                } catch (Exception e) {
                    System.err.println("登录异常: " + e.getMessage());
                }
            case Message.REGISTER:
                try {
                    return userController.handleRequest(request);
                } catch (Exception e) {
                    System.err.println("传递request异常: " + e.getMessage());
                }

            default:
                return Message.fromData(Message.RESPONSE, false, null, "未知请求类型: " + type);
        }
    }
}