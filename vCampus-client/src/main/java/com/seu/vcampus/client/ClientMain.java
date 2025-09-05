package com.seu.vcampus.client;

import com.seu.vcampus.client.view.frame.MainFrame;

public class ClientMain {
    public static void main(String[] args) {
        // 启动客户端GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}