package com.seu.vcampus.client.view.frame;

import com.seu.vcampus.common.model.User;
import com.seu.vcampus.client.view.panel.CoursePanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame(User currentUser) {
        initFrame();
        initContentPane(currentUser);
    }

    private void initFrame() {
        setTitle("VCampus - 学生选课系统");
        setSize(1000, 700);
        setLocationRelativeTo(null); // 居中显示
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));

        // 设置应用图标
        try {
            ImageIcon icon = new ImageIcon("resources/logo.png");
            if (icon.getImage() != null) {
                setIconImage(icon.getImage());
            }
        } catch (Exception e) {
            // 忽略图标加载失败
        }
    }

    private void initContentPane(User user) {
        // 创建选课面板，传入当前用户对象
        CoursePanel coursePanel = new CoursePanel(user);

        // 设置布局
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(coursePanel, BorderLayout.CENTER);

        // 添加状态栏
        JPanel statusBar = createStatusBar(user);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createStatusBar(User user) {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        JLabel userInfo = new JLabel("学生: " + user.getName() + " (" + user.getId() + ")");
        JLabel statusLabel = new JLabel("系统就绪");

        statusBar.add(userInfo, BorderLayout.WEST);
        statusBar.add(statusLabel, BorderLayout.EAST);

        return statusBar;
    }
}