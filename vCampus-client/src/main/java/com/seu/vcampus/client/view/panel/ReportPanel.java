// ReportPanel.java
package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.controller.AdminController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReportPanel extends JPanel {
    private AdminController adminController;
    private JButton generateButton;
    private JTextArea reportArea;

    public ReportPanel(AdminController adminController) {
        this.adminController = adminController;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 报表内容区域
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        // 生成按钮
        JPanel buttonPanel = new JPanel();
        generateButton = new JButton("生成报表");
        buttonPanel.add(generateButton);
        add(buttonPanel, BorderLayout.SOUTH);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
    }

    private void generateReport() {
        // 调用服务生成报表
        String report = adminController.generateReport();
        reportArea.setText(report);
    }
}