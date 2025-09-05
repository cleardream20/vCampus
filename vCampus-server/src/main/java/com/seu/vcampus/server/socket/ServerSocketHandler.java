package com.seu.vcampus.server.socket;

import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.ResponseCode;

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

                    // 从请求中获取User对象
                    User currentUser = (User) request.getData().get("user");
                    Message response = handleRequest(request, currentUser);

                    out.writeObject(response);
                    out.flush();
                    System.out.println("发送响应: " + response.getStatus());
                }
            } catch (IOException e) {
                System.out.println("客户端断开连接: " + socket.getRemoteSocketAddress());
            } catch (Exception e) {
                System.err.println("处理请求时出错: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("关闭Socket时出错: " + e.getMessage());
                }
            }
        }

        private Message handleRequest(Message request, User currentUser) {
            String prefix = request.getType().split("_")[0];

            // 公共接口（无需登录）
            if ("PUBLIC".equals(prefix)) {
                return handlePublicRequest(request);
            }

            // 需要登录的接口
            if (currentUser == null) {
                return createErrorResponse(request, ResponseCode.UNAUTHORIZED, "请先登录");
            }

            // 根据请求前缀分发
            switch (prefix) {
                case "STUDENT":
                    return handleStudentRequest(request, currentUser);
                case "TEACHER":
                    return handleTeacherRequest(request, currentUser);
                case "ADMIN":
                    return handleAdminRequest(request, currentUser);
                default:
                    return createErrorResponse(request, ResponseCode.BAD_REQUEST, "未知请求类型");
            }
        }

        private Message handlePublicRequest(Message request) {
            // 示例：登录接口
            if ("PUBLIC_LOGIN".equals(request.getType())) {
                // 实际开发中应验证密码，这里直接返回成功
                User user = (User) request.getData().get("user");
                Message response = new Message(request.getType());
                response.setStatus(ResponseCode.OK);
                response.addData("user", user); // 返回用户信息（含role）
                return response;
            }
            return createErrorResponse(request, ResponseCode.NOT_FOUND, "未知公共接口");
        }

        private Message handleStudentRequest(Message request, User user) {
            if (!"ST".equals(user.getRole())) {
                return createErrorResponse(request, ResponseCode.FORBIDDEN, "需要学生权限");
            }
            // 调用学生控制器
            return new Message(request.getType()); // 实际应调用控制器
        }

        private Message handleTeacherRequest(Message request, User user) {
            if (!"TC".equals(user.getRole())) {
                return createErrorResponse(request, ResponseCode.FORBIDDEN, "需要教师权限");
            }
            // 调用教师控制器
            return new Message(request.getType()); // 实际应调用控制器
        }

        private Message handleAdminRequest(Message request, User user) {
            if (!"AD".equals(user.getRole())) {
                return createErrorResponse(request, ResponseCode.FORBIDDEN, "需要管理员权限");
            }
            // 调用管理员控制器
            return new Message(request.getType()); // 实际应调用控制器
        }

        private Message createErrorResponse(Message request, int code, String msg) {
            Message response = new Message(request.getType());
            response.setStatus(code);
            response.setDescription(msg);
            return response;
        }
    }
}