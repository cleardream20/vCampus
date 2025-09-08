package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.view.frame.MainFrame;
import javax.swing.*;
import java.awt.*;

public class CourseAdminPanel extends JPanel {
    private final MainFrame mainFrame;  // 添加MainFrame引用

    // 修改构造函数，接收MainFrame参数
    public CourseAdminPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // 顶部返回按钮
        JButton backButton = new JButton("返回用户界面");
        backButton.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.showUserPanel();  // 直接调用已知的MainFrame方法
            }
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // 管理员功能选项卡
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("课程管理", new CourseManagementPanel());
        tabbedPane.addTab("选课统计", new SelectionReportPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}