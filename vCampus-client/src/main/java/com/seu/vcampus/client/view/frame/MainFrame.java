package com.seu.vcampus.client.view.frame;

import javax.swing.*;
import java.awt.*;

import com.seu.vcampus.client.service.LoginService;
import com.seu.vcampus.client.service.UserService;
import com.seu.vcampus.client.view.panel.UserManagementPanel;
import com.seu.vcampus.client.view.panel.course.CoursePanel;
import com.seu.vcampus.client.view.panel.dorm.DormPanel;
import com.seu.vcampus.client.view.panel.main.MainPanel;
import com.seu.vcampus.client.view.panel.RegisterPanel;
import com.seu.vcampus.client.view.panel.library.LibraryMainPanel; // 新的图书馆主面板
import com.seu.vcampus.client.view.panel.main.UserCenterPanel;
import com.seu.vcampus.client.view.panel.shop.ShopPanel;
import com.seu.vcampus.client.view.panel.student.ADPanel;
import com.seu.vcampus.client.view.panel.student.STPanel;
import com.seu.vcampus.common.model.Admin;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.model.Teacher;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.client.view.panel.login.LoginPanel;
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
    @Getter
    @Setter
    private Student currentStudent;
    @Getter
    @Setter
    private Teacher currentTeacher;
    @Getter
    @Setter
    private Admin currentAdmin;

    private CardLayout cardLayout;
    private JPanel mainPanel; // 主容器面板

    private LoginService  loginService;
    private UserService userService;

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private UserCenterPanel userCenterPanel;
    private UserManagementPanel userManagementPanel;
    // 声明新的 LibraryMainPanel 实例（延迟初始化）
    private LibraryMainPanel libraryMainPanel;
    private STPanel stPanel;
    private ADPanel adPanel;
    private CoursePanel coursePanel;
    private ShopPanel  shopPanel;
    private DormPanel dormPanel;

    // 私有的静态成员变量，用于存储单例实例
    private static volatile MainFrame instance;

    // 私有构造函数，防止外部实例化
    private MainFrame() {
        loginService = new LoginService();
        userService = new UserService();
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
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // 使用 CardLayout 来切换不同面板
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 初始化各个面板（不包括 LibraryMainPanel，因为需要用户信息）
        loginPanel = new LoginPanel();
        registerPanel = new RegisterPanel();
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
        mainPanel.add(new JLabel("加载中...", SwingConstants.CENTER), "USER_CENTER");
        mainPanel.add(new JLabel("加载中...", SwingConstants.CENTER), "USER_MANAGEMENT");
        mainPanel.add(new JLabel("加载中...", SwingConstants.CENTER), "SHOP");
        mainPanel.add(new JLabel("加载中...", SwingConstants.CENTER), "DORM");

        add(mainPanel);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // 弹出确认对话框
                int confirm = JOptionPane.showConfirmDialog(MainFrame.this,
                        "确定要退出吗？", "确认退出", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // 只有用户点击“是”，才执行登出并退出
                    if (currentUser != null &&
                            currentUser.getCid() != null &&
                            !currentUser.getCid().isEmpty()) {
                        loginService.logout(currentUser.getCid());
                    }
                    // 显式退出 JVM
                    System.exit(0);
                }
                // 如果点击“否”，什么也不做，窗口不会关闭
            }
        });
    }

    // 切换面板的方法
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public void showLoginPanel() {
        loginPanel.refreshPanel(new User());
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.revalidate();
        mainPanel.repaint();
        showPanel("LOGIN");
    }

    public void showRegisterPanel() {
        showPanel("REGISTER");
    }

    public void showMainPanel(User user) {
        this.currentUser = user;

        if (user.getRole().equals("ST")) this.currentStudent = userService.getStudentByUser(user);
        if (user.getRole().equals("TC")) this.currentTeacher = userService.getTeacherByUser(user);
        if (user.getRole().equals("AD")) this.currentAdmin = userService.getAdminByUser(user);

//        System.out.println("获取学生一卡通号：" + this.currentStudent.getCid());
//        System.out.println("获取教师一卡通号：" + this.currentTeacher.getCid());
//        System.out.println("获取管理员一卡通号：" + this.currentAdmin.getCid());

        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof MainPanel) {
                ((MainPanel) comp).refreshPanel(user);
                break;
            }
        }
        showPanel("MAIN");
    }

    public void showUserCenterPanel() {
        userCenterPanel = new UserCenterPanel();
        mainPanel.add(userCenterPanel, "USER_CENTER");
        mainPanel.revalidate();
        mainPanel.repaint();
        showPanel("USER_CENTER"); // 注意：如果你没有添加 USER_CENTER 面板，请确保已添加
    }

    public void showUserManagementPanel() {
        userManagementPanel = new UserManagementPanel();
        mainPanel.add(userManagementPanel, "USER_MANAGEMENT");
        mainPanel.revalidate();
        mainPanel.repaint();
        showPanel("USER_MANAGEMENT");
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

            // 获取用户角色 ("user" 或 "admin")

//        if (libraryMainPanel != null) {
//            mainPanel.remove(libraryMainPanel);
//        }

        libraryMainPanel = new LibraryMainPanel(currentUser);

        mainPanel.add(libraryMainPanel, "LIBRARY");

        mainPanel.revalidate();
        mainPanel.repaint();

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

    public void showDormPanel() {
        dormPanel = new DormPanel();
        mainPanel.add(dormPanel, "DORM");
        mainPanel.revalidate();
        mainPanel.repaint();
        showPanel("DORM");
    }

    public void showShopPanel() {
        shopPanel = new ShopPanel();
        mainPanel.add(shopPanel, "SHOP");
        mainPanel.revalidate();
        mainPanel.repaint();
        showPanel("SHOP");
    }
}