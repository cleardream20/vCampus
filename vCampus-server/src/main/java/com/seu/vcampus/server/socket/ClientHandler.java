package com.seu.vcampus.server.socket;

import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.server.controller.CourseController;
import com.seu.vcampus.server.controller.RequestController;
import com.seu.vcampus.server.controller.UserController;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理单个客户端请求的线程
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final UserController userController;
    private final CourseController courseController;
    private final Map<String, RequestController> controllerMap = new HashMap<>();

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.userController = new UserController(); // 可改为依赖注入
        this.courseController = new CourseController();
        initializeControllers();
    }

    private void initializeControllers() {
        addUserHandlers();
        addCourseHandlers();
    }

    private void addUserHandlers() {
        controllerMap.put(Message.LOGIN, userController);
        controllerMap.put(Message.REGISTER, userController);
        controllerMap.put(Message.LOGOUT, userController);
//        controllerMap.put(Message.GET_USER_INFO, userController);
//        controllerMap.put(Message.UPDATE_USER_INFO, userController);
        // 添加其他用户相关消息类型...
    }

    private void addCourseHandlers() {
        // 所有课程相关消息都路由到CourseController
        String[] courseMessageTypes = {
                Message.GET_COURSE_LIST,
                Message.SELECT_COURSE,
                Message.DROP_COURSE,
                Message.DROP_COURSE_AD,
                Message.GET_COURSE_BY_NAME,
                Message.GET_COURSE_BY_ID,
                Message.ADD_COURSE,
                Message.UPDATE_COURSE,
                Message.DELETE_COURSE,
                Message.GET_SELECTED_COURSES,
                Message.GET_COURSE_SCHEDULE,
                Message.GET_SELECTION_RECORDS,
                Message.GET_TEACHING_COURSES
        };

        for (String type : courseMessageTypes) {
            controllerMap.put(type, courseController);
        }
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

        // Map<String, Controller>
        RequestController requestController = controllerMap.get(type);
        if (requestController != null) {
            try {
                return requestController.handleRequest(request);
            } catch (SQLException e) {
                System.err.println("处理请求异常: " + e.getMessage());
                return Message.error(type, "服务器处理错误");
            }
        }

        return Message.error(type, "未知类型: " + type);
    }
}