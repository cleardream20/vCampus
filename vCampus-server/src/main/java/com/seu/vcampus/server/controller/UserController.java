package com.seu.vcampus.server.controller;

import com.google.gson.Gson;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.server.service.UserServiceImpl;
import lombok.Data;

import java.sql.SQLException;

public class UserController {
    private final UserServiceImpl userService = new UserServiceImpl();
    private final Gson gson = new Gson();

    @Data
    private static class LoginRequest {
        private String cid;
        private String password;
    }

    @Data
    private static class RegisterRequest {
        private String cid;
        private String password;
        private String tsid;
        private String confirmPassword;
        private String email;
        private String phone;
        private String name;
        private String role;
    }

    public String handleMessage(String requestJson) {
        try {
            Message request = Message.fromJson(requestJson);

            return switch (request.getType()) {
                case Message.TYPE_LOGIN -> handleLoginRequest(request);
                case Message.TYPE_REGISTER -> handleRegisterRequest(request);
                default -> createErrorResponse("暂未支持: " + request.getType());
            };
        } catch (Exception e) {
            return createErrorResponse("服务端请求处理异常: " + e.getMessage());
        }
    }

    private String handleLoginRequest(Message request) {
        try {
            LoginRequest loginRequest = gson.fromJson(request.getData(), LoginRequest.class);
            String cid = loginRequest.getCid();
            String password = loginRequest.getPassword();

            if(cid == null || password == null) {
                return createErrorResponse("一卡通号或密码不能为空！");
            }

            User user = userService.Login(cid, password);
            if(user != null) {
                Message response = new Message();
                response.setType(Message.TYPE_RESPONSE);
                response.setSuccess(true);
                response.setMessage("登陆成功");
                response.setData(gson.toJson(user));
                return response.toJson();
            } else {
                return createErrorResponse("一卡通号或密码错误");
            }

        } catch (SQLException e) {
            return createErrorResponse("数据库错误: " + e.getMessage());
        }
    }

    private String handleRegisterRequest(Message request) {
        try {
            RegisterRequest  registerRequest = gson.fromJson(request.getData(), RegisterRequest.class);
            String cid = registerRequest.getCid();
            String password = registerRequest.getPassword();
            String tsid = registerRequest.getTsid();
            String confirmPassword = registerRequest.getConfirmPassword();
            String email = registerRequest.getEmail();
            String phone = registerRequest.getPhone();
            String name = registerRequest.getName();
            String role = registerRequest.getRole();
            if(cid == null || password == null || confirmPassword == null || email == null || phone == null || name == null) {
                return createErrorResponse("请填写完整信息");
            } else if(password.length() < 6) {
                return createErrorResponse("密码长度不能小于6");
            } else if(!password.equals(confirmPassword)) {
                return createErrorResponse("前后两次密码不一样，请检查");
            } else {
                User user = new User(cid, password, tsid, name, email, phone, role);
                Message response = new Message();
                response.setType(Message.TYPE_RESPONSE);
                response.setSuccess(true);
                response.setMessage("注册成功");
                response.setData(gson.toJson(user));
                return response.toJson();
            }
        } catch (Exception e) {
            return createErrorResponse("数据库错误: " + e.getMessage());
        }
    }

    private String createErrorResponse(String errorMessage) {
        Message response = new Message();
        response.setType(Message.TYPE_RESPONSE);
        response.setSuccess(false);
        response.setMessage(errorMessage);
        return response.toJson();
    }
}
