package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.common.model.User;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CoursePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTabbedPane userTabbedPane;
    private JTable courseTable;
    private User currentUser; // 添加用户变量

    // 修改构造函数以接收用户信息
    public CoursePanel(User user) {
        this.currentUser = user; // 保存用户信息
        setLayout(new BorderLayout());
        initUI();

        // 根据用户角色决定初始界面
        if ("AD".equals(user.getRole())) {
            showAdminPanel(); // 管理员直接显示管理员界面
        } else {
            showUserPanel(); // 学生显示用户界面
        }
    }

    private void initUI() {
        // 使用CardLayout实现界面切换
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 创建用户界面卡片
        mainPanel.add(createUserPanel(), "user");
        // 创建管理员界面卡片
        mainPanel.add(createAdminPanel(), "admin");

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 顶部选项卡
        userTabbedPane = new JTabbedPane();
        userTabbedPane.addTab("课程查询", new CourseQueryPanel(currentUser));
        userTabbedPane.addTab("选课管理", new CourseSelectionPanel(currentUser));
        userTabbedPane.addTab("我的课表", new CourseSchedulePanel(currentUser));

        // 移除管理员登录按钮
        // 不再显示右上角的管理员登录按钮

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(userTabbedPane, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 移除返回用户界面按钮
        // 不再显示顶部的返回按钮

        // 管理员功能选项卡
        JTabbedPane adminTabbedPane = new JTabbedPane();

        // 创建课程管理面板 - 传递当前用户信息
        JPanel courseManagementPanel = new CourseManagementPanel(currentUser);
        adminTabbedPane.addTab("课程管理", courseManagementPanel);

        // 创建选课统计面板
        JPanel selectionReportPanel = new SelectionReportPanel(currentUser);
        adminTabbedPane.addTab("选课统计", selectionReportPanel);

        panel.add(adminTabbedPane, BorderLayout.CENTER);
        return panel;
    }

    public void showUserPanel() {
        cardLayout.show(mainPanel, "user");
    }

    public void showAdminPanel() {
        cardLayout.show(mainPanel, "admin");
    }
}