package com.seu.vcampus.server.controller;

import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Jsonable;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.server.service.UserService;
import com.seu.vcampus.server.service.UserServiceImpl;

import java.sql.SQLException;

public class UserController {

    private final UserService userService = new UserServiceImpl();

    public Message handleRequest(Message request) throws SQLException {
        String type = request.getType();

        switch (type) {
            case Message.LOGIN:
                User user = Jsonable.fromJson((String) request.getData(), User.class);
                if (user == null) {
                    return Message.fromData(type, false, null, "用户数据格式错误");
                }
                User result = userService.Login(user.getCid(), user.getPassword());
                if (result != null) {
                    return Message.fromData(Message.LOGIN, true, result, "登录成功");
                } else {
                    return Message.fromData(type, false, null, "账号或密码错误");
                }
            case Message.REGISTER:
                // userService.register(...)
                return Message.fromData(type, false, null, "注册功能开发中");
            default:
                return Message.fromData(Message.RESPONSE, false, null, "不支持的操作");
        }
    }

    // UserController.java 示例
    public Message handleLogin(Message request) throws SQLException {
        User user = Jsonable.fromJson(
                Jsonable.toJson(request.getData()),
                User.class
        );

        if (user == null) {
            return Message.error(Message.LOGIN, "用户数据无效");
        }

        User result = userService.Login(user.getCid(), user.getPassword());
        if (result == null || !result.getPassword().equals(user.getPassword())) {
            return Message.error(Message.LOGIN, "学号或密码错误");
        }

        // 直接传 User 对象，不要 toJson()
        return Message.success(Message.LOGIN, result, "登录成功");
    }
}