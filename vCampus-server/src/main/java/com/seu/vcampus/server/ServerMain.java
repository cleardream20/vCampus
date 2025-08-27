package com.seu.vcampus.server;

import com.seu.vcampus.common.model.User;

public class ServerMain {
    public static void main(String[] args) {
        User user = new User(
                "123456789123",
                "seu",
                "123@seu.edu.cn",
                "123456",
                18,
                "ST"
        );
        System.out.println(user);
    }
}
