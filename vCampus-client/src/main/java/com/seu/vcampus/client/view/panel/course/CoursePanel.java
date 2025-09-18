package com.seu.vcampus.client.view.panel.course;

import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.common.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CoursePanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTabbedPane userTabbedPane;
    private JTabbedPane adminTabbedPane; // 修复：添加缺失的成员变量
    private User currentUser;
    private JButton returnButton;

    // 定义刷新接口
    public interface Refreshable {
        void refreshData();
    }

    public CoursePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        initUI();
        initToolbar();

        switch (MainFrame.getInstance().getRoleToModule("Course")) {
            case "ADMIN":
                showAdminPanel();
                break;
            case "NORMAL":
                showUserPanel();
                break;
            case "NOADMIN":
                break;
        }
    }

    private void initToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT)); // 右对齐

        returnButton = new JButton("返回主界面");
        returnButton.addActionListener(this::handleReturnAction);
        toolBar.add(returnButton);

        add(toolBar, BorderLayout.NORTH); // 添加到顶部
    }

    private void handleReturnAction(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            MainFrame.getInstance().showMainPanel(currentUser);
        });
    }

    private void initUI() {
        // 使用标准CardLayout（移除自定义实现）
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 创建用户界面卡片
        mainPanel.add(createUserPanel(), "user");
        // 创建管理员界面卡片
        mainPanel.add(createAdminPanel(), "admin");

        add(mainPanel, BorderLayout.CENTER);
    }

    // 辅助方法：通过可见性判断当前卡片
    private void refreshCurrentCard() {
        for (Component comp : mainPanel.getComponents()) {
            if (comp.isVisible() && comp instanceof Refreshable) {
                ((Refreshable) comp).refreshData();
            }
        }
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 顶部选项卡
        userTabbedPane = new JTabbedPane();

        // 创建子面板（实现Refreshable接口）
        CourseQueryPanel queryPanel = new CourseQueryPanel(currentUser);
        CourseSelectionPanel selectionPanel = new CourseSelectionPanel(currentUser);
        CourseSchedulePanel schedulePanel = new CourseSchedulePanel(currentUser);

        userTabbedPane.addTab("课程查询", queryPanel);
        userTabbedPane.addTab("选课管理", selectionPanel);
        userTabbedPane.addTab("我的课表", schedulePanel);

        // 添加选项卡切换监听
        userTabbedPane.addChangeListener(e -> {
            Component selected = userTabbedPane.getSelectedComponent();
            if (selected instanceof Refreshable) {
                ((Refreshable) selected).refreshData();
            }
        });

        panel.add(userTabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        adminTabbedPane = new JTabbedPane(); // 修复：初始化成员变量

        // 创建子面板（实现Refreshable接口）
        CourseManagementPanel managementPanel = new CourseManagementPanel(currentUser);
        SelectionReportPanel reportPanel = new SelectionReportPanel(currentUser);

        adminTabbedPane.addTab("课程管理", managementPanel);
        adminTabbedPane.addTab("选课统计", reportPanel);

        // 添加选项卡切换监听
        adminTabbedPane.addChangeListener(e -> {
            Component selected = adminTabbedPane.getSelectedComponent();
            if (selected instanceof Refreshable) {
                ((Refreshable) selected).refreshData();
            }
        });

        panel.add(adminTabbedPane, BorderLayout.CENTER);
        return panel;
    }

    public void showUserPanel() {
        cardLayout.show(mainPanel, "user");
        refreshCurrentCard(); // 触发刷新

        // 默认刷新第一个选项卡
        if (userTabbedPane != null && userTabbedPane.getTabCount() > 0) {
            Component firstTab = userTabbedPane.getComponentAt(0);
            if (firstTab instanceof Refreshable) {
                ((Refreshable) firstTab).refreshData();
            }
        }
    }

    public void showAdminPanel() {
        cardLayout.show(mainPanel, "admin");
        refreshCurrentCard(); // 触发刷新

        // 默认刷新第一个选项卡
        if (adminTabbedPane != null && adminTabbedPane.getTabCount() > 0) {
            Component firstTab = adminTabbedPane.getComponentAt(0);
            if (firstTab instanceof Refreshable) {
                ((Refreshable) firstTab).refreshData();
            }
        }
    }
}