package com.seu.vcampus.server;

import com.seu.vcampus.server.socket.ServerSocketThread;
import javax.swing.*;

/**
 * 服务器主入口
 * 启动 Socket 服务，监听客户端连接
 */
public class ServerMain {

    public static void main(String[] args) {
        // 可选：启动一个简单的 GUI 显示服务器状态（非必须）
        showServerStatusWindow();

        // 启动 Socket 服务线程
        ServerSocketThread serverThread = new ServerSocketThread(8888);
        serverThread.start();

        System.out.println("vCampus 服务器已启动，监听端口 8888");
        System.out.println("等待客户端连接...");
    }

    /**
     * 可选：显示一个简单的服务器状态窗口
     */
    private static void showServerStatusWindow() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("vCampus Server");
            frame.setSize(300, 150);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            JLabel label = new JLabel("服务器运行中...", SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(16f));

            frame.add(label);
            frame.setVisible(true);
        });
    }
}