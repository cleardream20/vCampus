package com.seu.vcampus.client.view.frame;

import com.seu.vcampus.client.view.panel.CoursePanel;
import com.seu.vcampus.common.model.User;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        // 设置窗口标题
        super("vCampus 选课系统");

        // 设置窗口大小
        setSize(1000, 700);

        // 设置关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置布局
        setLayout(new BorderLayout());

        // 创建虚拟用户
        User virtualUser = createVirtualUser();

        // 创建并添加CoursePanel，传递用户信息
        CoursePanel coursePanel = new CoursePanel(virtualUser);
        add(coursePanel, BorderLayout.CENTER);

        // 居中显示
        setLocationRelativeTo(null);
    }

    // 创建虚拟用户
    private User createVirtualUser() {
        User user = new User();
        user.setId("ADMIN001");
        user.setName("系统管理员");
        user.setEmail("admin@vcampus.edu");
        user.setPassword("admin123");
        user.setAge(35);
        user.setRole("ST"); // 管理员角色
        return user;
    }

    public static void main(String[] args) {
        // 使用SwingUtilities.invokeLater确保线程安全
        SwingUtilities.invokeLater(() -> {
            MainFrame frame= new MainFrame();
            frame.setVisible(true);
        });
    }
}