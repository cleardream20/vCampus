package com.seu.vcampus.client.view.frame;

import javax.swing.*;
import java.awt.*;

import com.seu.vcampus.client.view.panel.CoursePanel;
import com.seu.vcampus.client.view.panel.MainPanel;
import com.seu.vcampus.client.view.panel.RegisterPanel;
import com.seu.vcampus.client.view.panel.library.LibraryMainPanel; // 新的图书馆主面板
import com.seu.vcampus.client.view.panel.student.ADPanel;
import com.seu.vcampus.client.view.panel.student.STPanel;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.client.view.panel.LoginPanel;
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

    // 声明新的 LibraryMainPanel 实例（延迟初始化）
    private LibraryMainPanel libraryMainPanel;
    private STPanel stPanel;
    private ADPanel adPanel;
    private CoursePanel coursePanel;

    // 私有的静态成员变量，用于存储单例实例
    private static volatile MainFrame instance;

    // 私有构造函数，防止外部实例化
    private MainFrame() {
        initializeUI();
        showLoginPanel();
    }

    // 提供全局访问点，获取唯一实例
    public static MainFrame getInstance() {
        if (instance == null) {
            synchronized (MainFrame.class) {
                if (instance == null) {
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

        // 初始化各个面板（不包括 LibraryMainPanel，因为需要用户信息）
        LoginPanel loginPanel = new LoginPanel();
        RegisterPanel registerPanel = new RegisterPanel();
        MainPanel userMainPanel = new MainPanel();

        // 添加基础面板
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");
        mainPanel.add(userMainPanel, "MAIN");

        // 先添加一个占位面板，名字为 "LIBRARY"
        // 后续会被 LibraryMainPanel 替换
        mainPanel.add(new JLabel("加载中...", SwingConstants.CENTER), "LIBRARY");
        mainPanel.add(new JLabel("加载中...", SwingConstants.CENTER), "STUDENT");
        mainPanel.add(new JLabel("加载中...", SwingConstants.CENTER), "COURSE");

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

    public void showRegisterPanel() {
        showPanel("REGISTER");
    }

    public void showMainPanel(User user) {
        this.currentUser = user;
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof MainPanel) {
                ((MainPanel) comp).refreshPanel(user);
                break;
            }
        }
        showPanel("MAIN");
    }

    public void showUserCenterPanel(User user) {
        currentUser = user;
        showPanel("USERCENTER"); // 注意：如果你没有添加 USERCENTER 面板，请确保已添加
    }

    /**
     * 显示图书馆主面板，根据用户角色动态构建
     */
    public void showLibraryPanel() {
//        // 空用户检查
//        if (currentUser == null) {
//            JOptionPane.showMessageDialog(this, "请先登录！", "未登录", JOptionPane.WARNING_MESSAGE);
//            showLoginPanel(); // 或跳转到登录页
//            return;
//        }

        String userId = currentUser.getCid();        // 获取用户ID
        String userRole = currentUser.getRole();     // 获取用户角色 ("user" 或 "admin")

//        if (libraryMainPanel != null) {
//            mainPanel.remove(libraryMainPanel);
//        }

        // 创建新的 LibraryMainPanel
        libraryMainPanel = new LibraryMainPanel(userId, userRole);

        // 将新面板添加到 "LIBRARY" 名称下（覆盖原有）
        mainPanel.add(libraryMainPanel, "LIBRARY");

        // 刷新布局
        mainPanel.revalidate();
        mainPanel.repaint();

        // 显示该面板
        showPanel("LIBRARY");
    }

    public void showStudentPanel() {
        String userId = currentUser.getCid();
        String userRole = currentUser.getRole();

        if (userRole == null) {
            stPanel = new STPanel();
            mainPanel.add(stPanel, "STUDENT");
        }

        else if (userRole.equals("ST") || userRole.equals("TC")) {
            stPanel = new STPanel();
            mainPanel.add(stPanel, "STUDENT");
        } else if ("AD".equals(userRole)) {
            adPanel = new ADPanel();
            mainPanel.add(adPanel, "STUDENT");
        }
        // 刷新布局
        mainPanel.revalidate();
        mainPanel.repaint();

        // 显示该面板
        showPanel("STUDENT");
    }

    public void showCoursePanel() {
        coursePanel = new CoursePanel(currentUser);
        mainPanel.add(coursePanel, "COURSE");
        mainPanel.revalidate();
        mainPanel.repaint();
        showPanel("COURSE");
    }
}