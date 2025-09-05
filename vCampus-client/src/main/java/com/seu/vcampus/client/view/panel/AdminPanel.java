// AdminPanel.java
package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.controller.AdminController;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    private AdminController adminController;
    private JTabbedPane tabbedPane;

    public AdminPanel(AdminController adminController) {
        this.adminController = adminController;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // 课程管理标签页
        CourseManagementPanel courseManagementPanel = new CourseManagementPanel(adminController);
        tabbedPane.addTab("课程管理", courseManagementPanel);

        // 规则配置标签页
        RuleConfigurationPanel ruleConfigurationPanel = new RuleConfigurationPanel(adminController);
        tabbedPane.addTab("规则配置", ruleConfigurationPanel);

        // 报表标签页
        ReportPanel reportPanel = new ReportPanel(adminController);
        tabbedPane.addTab("报表", reportPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
}