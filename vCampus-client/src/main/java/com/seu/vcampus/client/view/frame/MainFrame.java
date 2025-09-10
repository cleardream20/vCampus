package com.seu.vcampus.client.view.frame;

import javax.swing.*;
import java.awt.*;

import com.seu.vcampus.client.view.panel.MainPanel;
import com.seu.vcampus.client.view.panel.RegisterPanel;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.client.view.panel.LoginPanel;
import com.seu.vcampus.client.view.panel.ShopPanel;
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

    // 各个功能面板的引用
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private MainPanel mainContentPanel;
    private ShopPanel shopPanel; // 商店面板

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
        RegisterPanel registerPanel = new RegisterPanel();
        MainPanel mainPanel = new MainPanel();
        shopPanel = new ShopPanel(); // 初始化商店面板

        // 添加面板到主容器
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");
        mainPanel.add(mainPanel, "MAIN");
        mainPanel.add(shopPanel, "SHOP"); // 添加商店面板

        add(mainPanel);
    }

    // 切换面板的方法
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);

        // 如果切换到商店面板，更新商店面板的用户信息
        if ("STORE".equals(panelName) && currentUser != null) {
            shopPanel.setCurrentUser(currentUser);
            shopPanel.refreshProducts(); // 刷新商品列表
        }
    }

    // 具体的面板切换方法
    public void showLoginPanel() { showPanel("LOGIN"); }

    public void showRegisterPanel() { showPanel("REGISTER"); }

    public void showMainPanel(User user) {
        currentUser = user;
        showPanel("MAIN");

        // 更新主面板的用户信息
        if (mainContentPanel != null) {
            mainContentPanel.setCurrentUser(user);
        }
    }

    public void showShopPanel() {
        if (currentUser != null) {
            shopPanel.setCurrentUser(currentUser);
            shopPanel.refreshProducts();
            showPanel("SHOP");
        } else {
            JOptionPane.showMessageDialog(this,
                    "请先登录系统",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
            showLoginPanel();
        }
    }

    public void showUserCenterPanel(User user) {
        currentUser = user;
        showPanel("USERCENTER");
    }

    public void showLibraryPanel() { showPanel("LIBRARY"); }

    // 刷新当前用户信息
    public void refreshCurrentUser(User updatedUser) {
        this.currentUser = updatedUser;

        // 通知所有面板更新用户信息
        if (mainContentPanel != null) {
            mainContentPanel.setCurrentUser(updatedUser);
        }

        if (shopPanel != null) {
            shopPanel.setCurrentUser(updatedUser);
        }
    }

    // 注销登录
    public void logout() {
        currentUser = null;
        showLoginPanel();

        // 重置所有面板的状态
        if (loginPanel != null) {
            loginPanel.resetForm();
        }

        if (shopPanel != null) {
            shopPanel.clearCart();
        }
    }
}