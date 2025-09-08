package com.seu.vcampus.server.socket;

import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.ResponseCode;
import com.seu.vcampus.server.controller.CourseController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerSocketHandler {
    private final int port;
    private final Map<String, Object> controllers = new HashMap<>();

    public ServerSocketHandler(int port) {
        this.port = port;
    }

    public void addController(String type, Object controller) {
        controllers.put(type, controller);
    }

    public void start() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("服务器已启动，监听端口: " + port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("客户端连接: " + clientSocket.getRemoteSocketAddress());
                    new ClientHandler(clientSocket).start();
                }
            } catch (IOException e) {
                System.err.println("服务器启动失败: " + e.getMessage());
            }
        }).start();
    }

    private class ClientHandler extends Thread {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                while (true) {
                    Message request = (Message) in.readObject();
                    System.out.println("收到请求: " + request.getType());

                    // 从请求中获取用户信息
                    User user = (User) request.getData().get("user");

                    Message response = handleRequest(request, user);

                    out.writeObject(response);
                    out.flush();
                    System.out.println("发送响应: " + response.getStatus());
                }
            } catch (IOException e) {
                System.out.println("客户端断开连接: " + socket.getRemoteSocketAddress());
            } catch (Exception e) {
                System.err.println("处理请求时出错: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("关闭Socket时出错: " + e.getMessage());
                }
            }
        }

        private Message handleRequest(Message request, User user) {
            try {
                // 所有请求都直接转发给课程控制器
                CourseController courseController = (CourseController) controllers.get("COURSE");
                return courseController.handleCourseRequest(request, user);
            } catch (Exception e) {
                System.err.println("处理请求时发生异常: " + e.getMessage());
                e.printStackTrace();
                return createErrorResponse(request, ResponseCode.INTERNAL_SERVER_ERROR, "服务器内部错误");
            }
        }

        private Message createErrorResponse(Message request, int code, String msg) {
            Message response = new Message(request.getType());
            response.setStatus(code);
            response.setDescription(msg);
            return response;
        }
    }
}