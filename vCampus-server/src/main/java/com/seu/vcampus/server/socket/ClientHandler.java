package com.seu.vcampus.server.socket;

import com.seu.vcampus.common.util.LibraryMessage;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.ShopMessage;
import com.seu.vcampus.common.util.UserMessage;
import com.seu.vcampus.server.controller.*;

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
    private final LibraryController libraryController;
    private final ShopController shopController;
    private final Map<String, RequestController> controllerMap = new HashMap<>();

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.userController = new UserController(); // 可改为依赖注入
        this.courseController = new CourseController();
        libraryController = new LibraryController();
        shopController = new ShopController();
        initializeControllers();
    }

    private void initializeControllers() {
        addUserHandlers();
        addCourseHandlers();
        addLibraryHandlers();
        addShopHandlers();
    }

    private void addUserHandlers() {
        controllerMap.put(Message.LOGIN, userController);
        controllerMap.put(Message.REGISTER, userController);
        controllerMap.put(Message.LOGOUT, userController);
        controllerMap.put(UserMessage.GET_ST_BY_USER, userController);
        controllerMap.put(UserMessage.GET_TC_BY_USER, userController);
        controllerMap.put(UserMessage.GET_AD_BY_USER, userController);
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

    private void addLibraryHandlers() {
        String[] libraryMessageTypes = {
                LibraryMessage.GET_ALL_BOOKS,
                LibraryMessage.SEARCH_BOOKS,
                LibraryMessage.GET_BORROW_BOOKS,
                LibraryMessage.BORROW_BOOKS,
                LibraryMessage.RETURN_BOOK,
                LibraryMessage.ADD_BOOK,
                LibraryMessage.UPDATE_BOOK,
                LibraryMessage.DELETE_BOOK,
                LibraryMessage.RENEW_BOOK,
                LibraryMessage.GET_BOOKS_BY_ISBN,
                LibraryMessage.GET_RESERVATIONS,
                LibraryMessage.RESERVE_BOOKS,
                LibraryMessage.CANCEL_RESERVATION
        };

        for (String type : libraryMessageTypes) {
            controllerMap.put(type, libraryController);
        }
    }

    private void addShopHandlers() {
        String[] libraryMessageTypes = {
                ShopMessage.SHOP_GET_PRODUCTS,
                ShopMessage.SHOP_GET_PRODUCTS_BY_CATEGORY,
                ShopMessage.SHOP_GET_PRODUCT_DETAIL,
                ShopMessage.SHOP_ADD_TO_CART,
                ShopMessage.SHOP_GET_CART_ITEMS,
                ShopMessage.SHOP_UPDATE_CART_ITEM,
                ShopMessage.SHOP_REMOVE_FROM_CART,
                ShopMessage.SHOP_CLEAR_CART,
                ShopMessage.SHOP_CREATE_ORDER,
                ShopMessage.SHOP_GET_ORDERS,
                ShopMessage.SHOP_GET_ORDER_DETAIL
        };

        for (String type : libraryMessageTypes) {
            controllerMap.put(type, shopController);
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