package com.seu.vcampus.client.view.frame;

import javax.swing.*;
import java.awt.*;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.client.view.panel.LoginPanel;
import com.seu.vcampus.client.view.panel.StudentPanel.STPanel;
import lombok.Getter;

/**
 * MainFrame 单例模式
 * MainFrame mainFrame = MainFrame.getInstance();
 * mainFrame.setVisible(true);
 */
public class MainFrame extends JFrame {
    @Getter
    private User currentUser;
    private CardLayout cardLayout;
    private JPanel mainPanel; // 主容器面板

    // 私有的静态成员变量，用于存储单例实例
    private static volatile MainFrame instance;

    // 私有构造函数，防止外部实例化
    private MainFrame() {
        initializeUI();
        showLoginPanel();
    }

    // 提供全局访问点，获取唯一实例
    // 双重检查锁定方式
    public static MainFrame getInstance() {
        if (instance == null) { // 第一次检查
            synchronized (MainFrame.class) {
                if (instance == null) { // 第二次检查
                    instance = new MainFrame();
                }
            }
        }
        return instance;
    }

    private void initializeUI() {
        setTitle("虚拟校园系统");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 使用 CardLayout 来切换不同面板
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 初始化各个面板
        LoginPanel loginPanel = new LoginPanel();
        STPanel stPanel = new STPanel();
        // 初始化其他面板...

        // 添加面板到主容器
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(stPanel, "ST");
        // 添加其他面板...

        add(mainPanel);
    }

    // 切换面板的方法
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    // 具体的面板切换方法
    public void showLoginPanel() {
        showPanel("LOGIN");
    }

    public void showSTPanel() {
        showPanel("ST");
    }

    public void showLibraryPanel() {
        showPanel("LIBRARY");
    }

    public void showMainPanel(User user) {
        this.currentUser = user;
        setTitle("虚拟校园系统 - " + user.getTsid());
        // 这里可以切换到主功能面板
    }
}