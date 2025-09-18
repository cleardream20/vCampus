package com.seu.vcampus.server.controller;

import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Jsonable;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.UserMessage;
import com.seu.vcampus.server.service.UserService;
import com.seu.vcampus.server.service.UserServiceImpl;

import java.sql.SQLException;

public class UserController implements RequestController {

    private final UserService userService = new UserServiceImpl();

    @Override
    public Message handleRequest(Message request) throws SQLException {
        String type = request.getType();

        return switch (type) {
            case Message.LOGIN -> handleLogin(request);
            case Message.REGISTER -> handleRegister(request);
            case Message.LOGOUT -> handleLogout(request);
            case UserMessage.GET_ST_BY_USER, UserMessage.GET_TC_BY_USER, UserMessage.GET_AD_BY_USER, UserMessage.GET_ALL_USER -> handleGetUser(request);
            case UserMessage.GET_USER_BY_PHONE, UserMessage.GET_USER_BY_EMAIL ->  handleGetUserByEP(request);
            default -> Message.fromData(Message.RESPONSE, false, null, "不支持的操作");
        };
    }

    public Message handleLogin(Message request) throws SQLException {
        User user = Jsonable.fromJson(
                Jsonable.toJson(request.getData()),
                User.class
        );

        if (user == null) {
            return Message.error(Message.LOGIN, "用户数据无效");
        }

//        ClientSession existingSession = UserService.getOnlineUser(user.getCid());
//        if(existingSession != null) {
//            Message kickMsg = Message.error(Message.KICKED, "账号在别处登录");
//            return kickMsg;
//        }

        // 账号别处登录，踢出
        if(userService.checkOnlineUser(user.getCid())) return Message.error(Message.KICKED, "账号在别处登录");

        User result = userService.login(user.getCid(), user.getPassword());
        if (result == null || !result.getPassword().equals(user.getPassword())) {
            return Message.error(Message.LOGIN, "学号或密码错误");
        }

        // 直接传 User 对象，不要 toJson()
        return Message.success(Message.LOGIN, result, "登录成功");
    }
    public Message handleRegister(Message request) throws SQLException {
        User user = Jsonable.fromJson(Jsonable.toJson(request.getData()), User.class);
        if (user == null) return Message.error(Message.REGISTER, "用户数据无效");
        User result = userService.register(user);
        if (result == null || !result.getPassword().equals(user.getPassword())) return Message.error(Message.REGISTER, "注册数据异常");

        return Message.success(Message.REGISTER, result, "注册成功");
    }

    public Message handleLogout(Message request) throws SQLException {
        String cid = Jsonable.fromJson(Jsonable.toJson(request.getData()), String.class);
        if (cid == null) return Message.error(Message.LOGOUT, "用户数据无效");
        userService.logout(cid);

        return Message.success(Message.REGISTER, null, "登出成功");
    }

    public Message handleGetUser(Message request) throws SQLException {
        if (request.getType().equals(UserMessage.GET_ALL_USER)) return Message.success(request.getType(), userService.getAllUsers(), "获取所有用户信息成功");
        User user = Jsonable.fromJson(Jsonable.toJson(request.getData()), User.class);
        if (user == null) return Message.error(request.getType(), "用户为空");
        return switch (request.getType()) {
            case UserMessage.GET_ST_BY_USER ->
                    Message.success(request.getType(), userService.getStudentByUser(user), "获取学生信息成功");
            case UserMessage.GET_TC_BY_USER ->
                    Message.success(request.getType(), userService.getTeacherByUser(user), "获取教师信息成功");
            case UserMessage.GET_AD_BY_USER ->
                    Message.success(request.getType(), userService.getAdminByUser(user), "获取管理员信息成功");
            default -> Message.error(request.getType(), "未定义的类型");
        };
    }

    public Message handleGetUserByEP(Message request) throws SQLException {
        String ep = Jsonable.fromJson(Jsonable.toJson(request.getData()), String.class);
        if (ep == null) return Message.error(request.getType(), "邮箱或电话为空");
        return switch (request.getType()) {
            case UserMessage.GET_USER_BY_EMAIL ->
                Message.success(request.getType(), userService.getUserByEmail(ep), "通过邮箱获取用户信息成功");
            case UserMessage.GET_USER_BY_PHONE ->
                Message.success(request.getType(), userService.getUserByPhone(ep), "通过电话获取用户信息成功");
            default -> Message.error(request.getType(), "未定义的类型");
        };
    }
}