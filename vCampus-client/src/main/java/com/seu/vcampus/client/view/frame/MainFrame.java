package com.seu.vcampus.client.view.frame;

import javax.swing.*;
import java.awt.*;

import com.seu.vcampus.client.view.panel.MainPanel;
import com.seu.vcampus.client.view.panel.RegisterPanel;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.client.view.panel.LoginPanel;
import com.seu.vcampus.client.view.panel.ShopPanel;
import lombok.Getter;
import lombok.Setter;
/**
 * MainFrame 单例模式
 * MainFrame mainFrame = MainFrame.getInstance();
 * mainFrame.setVisible(true);
 */
public class MainFrame extends JFrame {
    @Getter
    @Setter
    private User currentUser;
    private CardLayout cardLayout;
    private JPanel mainPanel; // 主容器面板

    // 各个功能面板的引用
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private MainPanel mainContentPanel;
    private ShopPanel shopPanel; // 商店面板

    // 私有的静态成员变量，用于存储单例实例
    private static class MainFrameHolder {
        private static final MainFrame INSTANCE = new MainFrame();
    }
    // 私有构造函数，防止外部实例化
    private MainFrame() {
        // 先设置基本属性
        setTitle("虚拟校园系统");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 初始化UI组件
        initializeUI();

        // 显示登录面板
        showLoginPanel();
    }

    // 提供全局访问点，获取唯一实例
    // 双重检查锁定方式
    // 提供全局访问点
    public static MainFrame getInstance() {
        return MainFrameHolder.INSTANCE;
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
        mainContentPanel = new MainPanel();
        shopPanel = createShopPanel(); // 初始化商店面板

        // 添加面板到主容器
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");
        mainPanel.add(mainContentPanel, "MAIN");
        mainPanel.add(shopPanel, "SHOP"); // 添加商店面板

        add(mainPanel);
    }

    // 创建商店面板的工厂方法
    private ShopPanel createShopPanel() {
        ShopPanel panel = new ShopPanel();
        // 可以进行其他初始化操作
        return panel;
    }

    // 切换面板的方法
    public void showPanel(String panelName) {
        System.out.println("切换到面板: " + panelName);
        cardLayout.show(mainPanel, panelName);

        // 添加面板切换后的回调
        if ("SHOP".equals(panelName) && currentUser != null) {
            // 延迟一点时间确保面板已显示
            Timer timer = new Timer(100, e -> {
                if (shopPanel != null) {
                    shopPanel.setCurrentUser(currentUser);
                    shopPanel.refreshProducts();
                }
            });
            timer.setRepeats(false);
            timer.start();
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
            // 确保在 EDT 中执行界面更新
            SwingUtilities.invokeLater(() -> {
                showPanel("STORE");

                // 更新商店面板的用户信息
                if (shopPanel != null) {
                    shopPanel.setCurrentUser(currentUser);
                    shopPanel.refreshProducts();
                }
            });
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