package com.seu.vcampus.client;

import com.seu.vcampus.client.view.frame.MainFrame;

import javax.swing.*;

public class ClientMain {
    // 默认服务器配置
    private static final String DEFAULT_SERVER_HOST = "localhost";
    private static final int DEFAULT_SERVER_PORT = 8888;

    public static void main(String[] args) {
        // 解析命令行参数获取服务器配置
        String serverHost = DEFAULT_SERVER_HOST;
        int serverPort = DEFAULT_SERVER_PORT;

        if (args.length >= 1) {
            serverHost = args[0];
        }

        if (args.length >= 2) {
            try {
                serverPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("警告：端口参数无效，使用默认端口 " + DEFAULT_SERVER_PORT);
                serverPort = DEFAULT_SERVER_PORT;
            }
        }

        // 创建最终变量用于 lambda 表达式
        final String finalServerHost = serverHost;
        final int finalServerPort = serverPort;

        // 启动客户端GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                // 创建主窗口并传入服务器配置
                MainFrame frame = new MainFrame(finalServerHost, finalServerPort);
                frame.setVisible(true);
            } catch (Exception e) {
                // 显示错误对话框
                JOptionPane.showMessageDialog(
                        null,
                        "客户端启动失败: " + e.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        });
    }
}