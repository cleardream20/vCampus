package com.seu.vcampus.client;

import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.client.view.panel.student.ADPanel;

import javax.swing.*;
import java.awt.*;

public class ClientMain {
    public static void main(String[] args) {
//        System.out.println(System.getProperty("java.io.tmpdir"));

        // 1. 设置 Swing 系统外观（可选，让界面更美观）
        setSystemLookAndFeel();

        // 2. 使用 SwingUtilities 确保 GUI 在事件调度线程（EDT）中创建
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = MainFrame.getInstance();
                mainFrame.setVisible(true); // 显示主窗口（默认显示登录页）

//                JFrame jFrame = new JFrame();
//                jFrame.setVisible(true);
//                jFrame.setSize(800, 600);
//                ADPanel adPanel = new ADPanel();
//                jFrame.add(adPanel);

            } catch (Exception e) {
                showErrorAndExit("客户端启动失败", e);
            }
        });
    }

    /**
     * 设置系统原生外观（Windows/Linux/Mac 的本地风格）
     */
    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("无法设置系统外观，使用默认风格: " + e.getMessage());
            // 忽略，使用默认 Metal 风格
        }
    }

    /**
     * 显示错误对话框并退出
     */
    private static void showErrorAndExit(String message, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(
                null,
                message + "：\n" + e.getMessage(),
                "启动错误",
                JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
    }
}