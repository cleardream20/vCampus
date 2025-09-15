package com.seu.vcampus.client.controller;

import com.seu.vcampus.common.model.User;

public class LoginController {
    public User login(String cid, String password){
        return new User();
    }
//    public boolean login(String cid, String password) {
//        try {
//            User user = new User();
//            user.setCid(cid);
//            user.setPassword(password);
//
//            // 发送登录请求到服务器
//            clientSocketHandler.sendMessage("LOGIN", user);
//
//            // 接收服务器响应
//            Object response = clientSocketHandler.receiveMessage();
//            if (response instanceof User) {
//                User loggedInUser = (User) response;
//                return loggedInUser != null && loggedInUser.getCid() != null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public boolean logout(User user) {
//        try {
//            clientSocketHandler.sendMessage("LOGOUT", user);
//            Object response = clientSocketHandler.receiveMessage();
//            return response instanceof Boolean && (Boolean) response;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
}