package com.seu.vcampus.client.service;

import com.google.gson.JsonObject;
import com.seu.vcampus.client.socket.ClientSocketUtil;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Jsonable;
import com.seu.vcampus.common.util.Message;

import java.io.IOException;

public class LoginService {
    public User login(String cid, String password) throws Exception {
        if(cid == null || cid.trim().isEmpty()) {
            throw new IllegalArgumentException("一卡通号不能为空");
        }
        if(password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        // 构造请求数据
//        JsonObject data = new JsonObject();
//        data.addProperty("cid", cid.trim());
//        data.addProperty("password", password.trim());
        User user = new User();
        user.setCid(cid);
        user.setPassword(password);

        Message request = Message.success(Message.LOGIN, user, "尝试登录");

        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if(response == null) {
                throw new Exception("服务器无响应");
            }

            if(!response.isSuccess()) {
                if(response.getType().equals(Message.KICKED)) {
                    throw new Exception("账号在别处登录");
                }

                throw new Exception(response.getMessage() != null ?
                        response.getMessage() : "登录失败");
            }

            user = Jsonable.fromJson(
                    Jsonable.toJson(response.getData()),
                    User.class
            );

            if(user == null) {
                throw new Exception("用户信息解析失败");
            }

            return user;
        } catch (IOException e) {
            throw new Exception("无法连接至服务器");
        }
    }

    public boolean register(User user) {
        if (user == null) {
            System.err.println("注册失败：用户信息为空");
            return false;
        }

        String cid = user.getCid();
        String password = user.getPassword();

        if (cid == null || cid.trim().isEmpty()) {
            System.err.println("注册失败：一卡通号不能为空");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            System.err.println("注册失败：密码不能为空");
            return false;
        }

        // 构造注册请求
        Message request = Message.success(Message.REGISTER, user, "用户注册请求");

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response == null) {
                System.err.println("注册失败：服务器无响应");
                return false;
            }

            if (response.isSuccess()) {
                System.out.println("注册成功：" + response.getMessage());
                return true;
            } else {
                System.err.println("注册失败：" + response.getMessage());
                return false;
            }

        } catch (IOException e) {
            System.err.println("注册失败：无法连接服务器 - " + e.getMessage());
            return false;
        }
    }

    public boolean logout(String cid) {
        if (cid == null || cid.trim().isEmpty()) {
            System.err.println("登出失败：一卡通号（用户）不能为空");
            return false;
        }
        Message request = Message.success(Message.LOGOUT, cid, "用户登出请求");
        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response == null) {
                System.err.println("登出失败：服务器无响应");
                return false;
            }

            if (response.isSuccess()) {
                System.out.println("登出成功：" + response.getMessage());
                return true;
            } else {
                System.err.println("登出失败：" + response.getMessage());
                return false;
            }

        } catch (IOException e) {
            System.err.println("登出失败：无法连接服务器 - " + e.getMessage());
            return false;
        }
    }
}
