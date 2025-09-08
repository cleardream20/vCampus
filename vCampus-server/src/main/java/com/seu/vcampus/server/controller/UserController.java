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
            case Message.TYPE_LOGIN:
                User user = Jsonable.fromJson(request.getData(), User.class);
                if (user == null) {
                    return Message.fromData(type, "no", false, "用户数据格式错误");
                }
                User result = userService.Login(user.getCid(), user.getPassword());
                if (result != null) {
                    return Message.fromData(Message.TYPE_LOGIN, result.toJson(), true, "登录成功");
                } else {
                    return Message.fromData(type, "no", false, "账号或密码错误");
                }
            case Message.TYPE_REGISTER:
                // userService.register(...)
                return Message.fromData(type, "no", false, "注册功能开发中");
            default:
                return Message.fromData(Message.TYPE_RESPONSE, "no", false, "不支持的操作");
        }
    }
}