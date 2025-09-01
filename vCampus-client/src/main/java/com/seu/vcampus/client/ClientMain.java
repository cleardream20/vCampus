package com.seu.vcampus.client;

import com.seu.vcampus.client.socket.ClientSocketHandler;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.client.view.frame.MainFrame;

import javax.swing.*;

public class ClientMain {
    public static void main(String[] args) {
        // 初始化网络连接
        ClientSocketHandler.getInstance();

        // 创建模拟用户
        User currentUser = createMockUser();

        // 启动主界面
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(currentUser);
            mainFrame.setVisible(true);
        });
    }

    private static User createMockUser() {
        User user = new User();
        user.setId("20210001");
        user.setName("张三");
        user.setRole("ST"); // 学生
        return user;
    }
}