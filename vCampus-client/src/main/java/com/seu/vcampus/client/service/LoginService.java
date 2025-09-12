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
            throw new IllegalArgumentException("cid is null or empty");
        }
        if(password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("password is null or empty");
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
                throw new Exception(response.getMessage() != null ?
                        response.getMessage() : "登录失败");
            }

            user = Jsonable.fromJson(
                    Jsonable.toJson(response.getData()),
                    User.class
            );

//            String userData = (String) response.getData();
//            if(userData == null || userData.trim().isEmpty()) {
//                throw new Exception("出错：未返回用户信息");
//            }
//
//            user = Jsonable.fromJson(userData, User.class);
            if(user == null) {
                throw new Exception("用户信息解析失败");
            }

            return user;
        } catch (IOException e) {
            throw new Exception("无法连接至服务器");
        }
    }
}
