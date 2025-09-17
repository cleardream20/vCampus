package com.seu.vcampus.client.view.panel.dorm;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class DormAdminPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // 管理端卡片名称常量
    private static final String CARD_HOME = "ADMIN_HOME";
    private static final String CARD_APPROVAL = "APPROVAL";
    private static final String CARD_SERVICE_MGMT = "SERVICE_MGMT";
    private static final String CARD_INFO_VIEW = "INFO_VIEW";

    public DormAdminPanel() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // 创建顶部导航栏（管理端使用不同的颜色主题）
        JPanel navPanel = createAdminNavPanel();
        add(navPanel, BorderLayout.NORTH);

        // 创建主卡片面板
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(245, 245, 245));

        // 创建并添加所有管理端子页面
        cardPanel.add(createAdminHomePanel(), CARD_HOME);
        cardPanel.add(createApprovalPanel(), CARD_APPROVAL);
        cardPanel.add(createServiceMgmtPanel(), CARD_SERVICE_MGMT);
        cardPanel.add(createInfoViewPanel(), CARD_INFO_VIEW);

        // 默认显示管理端首页
        cardLayout.show(cardPanel, CARD_HOME);
        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createAdminNavPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        panel.setBackground(new Color(255, 230, 230)); // 浅红色背景区分管理端

        JButton btnHome = new JButton("管理首页");
        JButton btnBack = new JButton("返回");

        btnHome.setBackground(new Color(255, 210, 210));
        btnBack.setBackground(new Color(255, 210, 210));
        btnHome.setFocusPainted(false);
        btnBack.setFocusPainted(false);
        btnHome.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btnBack.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        btnHome.addActionListener(e -> cardLayout.show(cardPanel, CARD_HOME));
        btnBack.addActionListener(e -> cardLayout.show(cardPanel, CARD_HOME));

        panel.add(btnHome);
        panel.add(btnBack);

        // 添加标题
        JLabel title = new JLabel("宿舍管理系统 - 管理端");
        title.setFont(new Font("微软雅黑", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        panel.add(title);

        return panel;
    }

    /**
     * 1. 管理端首页
     */
    private JPanel createAdminHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 欢迎标题
        JLabel welcomeLabel = new JLabel("宿舍管理控制台", JLabel.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(200, 0, 0));
        gbc.gridwidth = 2;
        panel.add(welcomeLabel, gbc);

        // 住宿申请审核功能区
        gbc.gridy++;
        gbc.gridwidth = 1;
        JPanel approvalSection = createAdminSectionPanel("住宿申请审核", new Color(255, 230, 230));
        JButton btnToApproval = new JButton("处理申请");
        btnToApproval.addActionListener(e -> cardLayout.show(cardPanel, CARD_APPROVAL));
        approvalSection.add(btnToApproval, BorderLayout.CENTER);
        panel.add(approvalSection, gbc);

        // 宿舍服务处理功能区
        gbc.gridx = 1;
        JPanel serviceSection = createAdminSectionPanel("宿舍服务处理", new Color(230, 230, 255));
        JButton btnToServiceMgmt = new JButton("处理服务");
        btnToServiceMgmt.addActionListener(e -> cardLayout.show(cardPanel, CARD_SERVICE_MGMT));
        serviceSection.add(btnToServiceMgmt, BorderLayout.CENTER);
        panel.add(serviceSection, gbc);

        // 住宿情况查询功能区
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel infoSection = createAdminSectionPanel("住宿情况查询", new Color(230, 255, 230));
        JButton btnToInfoView = new JButton("查看住宿情况");
        btnToInfoView.addActionListener(e -> cardLayout.show(cardPanel, CARD_INFO_VIEW));
        infoSection.add(btnToInfoView, BorderLayout.CENTER);
        panel.add(infoSection, gbc);

        return panel;
    }

    private JPanel createAdminSectionPanel(String title, Color bgColor) {
        JPanel section = new JPanel(new BorderLayout(10, 10));
        section.setBackground(bgColor);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        section.setPreferredSize(new Dimension(250, 120));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setForeground(new Color(70, 70, 70));
        section.add(titleLabel, BorderLayout.NORTH);

        return section;
    }

    /**
     * 申请审核页面
     */
    private JPanel createApprovalPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("住宿申请审核", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        title.setForeground(new Color(200, 0, 0));
        panel.add(title, BorderLayout.NORTH);

        // 表头
        String[] columns = {"申请ID", "学号", "申请类型", "申请时间", "状态"};
        Object[][] data = {

        };

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        // 设置表头样式
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 13));
        header.setBackground(new Color(255, 210, 210));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);

        // 添加操作按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton approveBtn = new JButton("批准");
        JButton rejectBtn = new JButton("拒绝");

        approveBtn.setBackground(new Color(100, 200, 100));
        approveBtn.setForeground(Color.WHITE);
        rejectBtn.setBackground(new Color(255, 100, 100));
        rejectBtn.setForeground(Color.WHITE);

        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);

        // 添加按钮事件
        approveBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String applicationId = (String) table.getValueAt(selectedRow, 0);
                JOptionPane.showMessageDialog(panel, "已批准申请: " + applicationId);
                // 更新表格状态
                table.setValueAt("已批准", selectedRow, 4);
            } else {
                JOptionPane.showMessageDialog(panel, "请先选择一行申请");
            }
        });

        rejectBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String applicationId = (String) table.getValueAt(selectedRow, 0);
                JOptionPane.showMessageDialog(panel, "已拒绝申请: " + applicationId);
                table.setValueAt("已拒绝", selectedRow, 4);
            } else {
                JOptionPane.showMessageDialog(panel, "请先选择一行申请");
            }
        });

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 服务管理页面
     */
    private JPanel createServiceMgmtPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("宿舍服务处理", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        title.setForeground(new Color(200, 0, 0));
        panel.add(title, BorderLayout.NORTH);

        // 表头
        String[] columns = {"服务ID", "学号", "问题描述", "提交时间", "状态", "负责人"};
        Object[][] data = {

        };

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        // 设置表头样式
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 13));
        header.setBackground(new Color(230, 230, 255));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);

        // 添加操作按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton processBtn = new JButton("开始处理");
        JButton completeBtn = new JButton("标记完成");

        processBtn.setBackground(new Color(100, 180, 255));
        processBtn.setForeground(Color.WHITE);
        completeBtn.setBackground(new Color(100, 200, 100));
        completeBtn.setForeground(Color.WHITE);

        buttonPanel.add(processBtn);
        buttonPanel.add(completeBtn);

        processBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String serviceId = (String) table.getValueAt(selectedRow, 0);
                JOptionPane.showMessageDialog(panel, "开始处理服务: " + serviceId);
                table.setValueAt("处理中", selectedRow, 4);
            } else {
                JOptionPane.showMessageDialog(panel, "请先选择一行服务");
            }
        });

        completeBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String serviceId = (String) table.getValueAt(selectedRow, 0);
                JOptionPane.showMessageDialog(panel, "标记服务完成: " + serviceId);
                table.setValueAt("已完成", selectedRow, 4);
            } else {
                JOptionPane.showMessageDialog(panel, "请先选择一行服务");
            }
        });

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 住宿信息查看页面
     */
    private JPanel createInfoViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("学生住宿情况", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        title.setForeground(new Color(200, 0, 0));
        panel.add(title, BorderLayout.NORTH);

        // 搜索栏
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.add(new JLabel("搜索学生:"));

        JTextField searchField = new JTextField(15);
        searchPanel.add(searchField);

        JButton searchBtn = new JButton("搜索");
        searchBtn.setBackground(new Color(100, 180, 255));
        searchBtn.setForeground(Color.WHITE);
        searchPanel.add(searchBtn);

        panel.add(searchPanel, BorderLayout.NORTH);

        // 学生住宿信息表格
        String[] columns = {"学号", "姓名", "楼栋", "房间号", "床位号", "状态"};
        Object[][] data = {

        };

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        // 设置表头样式
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 13));
        header.setBackground(new Color(230, 255, 230));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}