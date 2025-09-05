package com.seu.vcampus.client.view.frame;

import com.seu.vcampus.client.controller.CourseController;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.client.socket.ClientSocketHandler;
import com.seu.vcampus.client.view.panel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private User currentUser;
    private ClientSocketHandler socketHandler;

    public MainFrame() {
        initializeUI();
        // 直接进入选课模块
        enterCourseSelectionDirectly();
    }

    private void initializeUI() {
        setTitle("vCampus 校园管理系统");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示

        // 初始化主面板
        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // 添加窗口关闭监听器
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeSocketConnection();
            }
        });
    }

    private void enterCourseSelectionDirectly() {
        // 创建默认学生用户
        currentUser = new User();
        currentUser.setId("20230001");
        currentUser.setName("张三");
        currentUser.setRole("ST");

        // 初始化Socket连接
        if (socketHandler == null) {
            socketHandler = new ClientSocketHandler("localhost", 8888);
        }

        // 直接显示选课面板
        switchToCoursePanel();
    }

    private void switchToCoursePanel() {
        mainPanel.removeAll();

        // 创建课程控制器
        CourseController courseController = new CourseController(socketHandler);

        // 创建选课面板
        CoursePanel coursePanel = new CoursePanel(courseController, currentUser);
        mainPanel.add(coursePanel, BorderLayout.CENTER);

        // 添加顶部菜单栏
        addMenuBar();

        revalidate();
        repaint();
    }

    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // 用户菜单
        JMenu userMenu = new JMenu("用户");
        JMenuItem logoutItem = new JMenuItem("退出系统");
        logoutItem.addActionListener(e -> System.exit(0));
        userMenu.add(logoutItem);
        menuBar.add(userMenu);

        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void closeSocketConnection() {
        if (socketHandler != null) {
            socketHandler.close();
            socketHandler = null;
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "vCampus 校园管理系统\n版本 1.0\n© 2023 SEU",
                "关于",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}