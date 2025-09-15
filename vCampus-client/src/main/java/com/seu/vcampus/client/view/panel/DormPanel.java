package com.seu.vcampus.client.view.panel;

import javax.swing.*;
import javax.swing.table.JTableHeader;

import java.awt.*;

public class DormPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // 用户端卡片名称常量
    private static final String CARD_HOME = "HOME";
    private static final String CARD_INFO_DISPLAY = "INFO_DISPLAY";
    private static final String CARD_APPLICATION = "APPLICATION";
    private static final String CARD_APPLICATION_STATUS = "APP_STATUS";
    private static final String CARD_SERVICE = "SERVICE";
    private static final String CARD_SERVICE_STATUS = "SERVICE_STATUS";

    public DormPanel() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // 1. 创建顶部导航栏
        JPanel navPanel = createNavPanel();
        add(navPanel, BorderLayout.NORTH);

        // 2. 创建主卡片面板
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(245, 245, 245));

        // 3. 创建并添加所有子页面
        cardPanel.add(createHomePanel(), CARD_HOME);
        cardPanel.add(createInfoDisplayPanel(), CARD_INFO_DISPLAY);
        cardPanel.add(createApplicationPanel(), CARD_APPLICATION);
        cardPanel.add(createApplicationStatusPanel(), CARD_APPLICATION_STATUS);
        cardPanel.add(createServicePanel(), CARD_SERVICE);
        cardPanel.add(createServiceStatusPanel(), CARD_SERVICE_STATUS);

        // 4. 默认显示首页
        cardLayout.show(cardPanel, CARD_HOME);
        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createNavPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        panel.setBackground(new Color(220, 240, 255));

        JButton btnHome = createNavButton("首页", new Color(200, 230, 255));
        JButton btnBack = createNavButton("返回", new Color(200, 230, 255));

        btnHome.addActionListener(e -> cardLayout.show(cardPanel, CARD_HOME));
        btnBack.addActionListener(e -> cardLayout.show(cardPanel, CARD_HOME));

        panel.add(btnHome);
        panel.add(btnBack);
        
        // 添加标题
        JLabel title = new JLabel("学生宿舍管理系统");
        title.setFont(new Font("微软雅黑", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        panel.add(title);
        
        return panel;
    }

    private JButton createNavButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }

    /**
     * 1. 我的宿舍首页
     */
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 欢迎标题
        JLabel welcomeLabel = new JLabel("欢迎使用宿舍管理系统", JLabel.CENTER);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        welcomeLabel.setForeground(new Color(0, 100, 200));
        gbc.gridwidth = 2;
        panel.add(welcomeLabel, gbc);

        // 功能区1: 住宿信息与管理
        gbc.gridy++;
        gbc.gridwidth = 1;
        JPanel infoSection = createHomeSectionPanel("住宿信息与管理", new Color(230, 245, 255));
        JButton btnToInfo = new JButton("查看住宿信息");
        JButton btnToAppStatus = new JButton("查看申请状态");
        btnToInfo.addActionListener(e -> cardLayout.show(cardPanel, CARD_INFO_DISPLAY));
        btnToAppStatus.addActionListener(e -> cardLayout.show(cardPanel, CARD_APPLICATION_STATUS));
        
        JPanel infoBtnPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoBtnPanel.setOpaque(false);
        infoBtnPanel.add(btnToInfo);
        infoBtnPanel.add(btnToAppStatus);
        infoSection.add(infoBtnPanel, BorderLayout.CENTER);
        panel.add(infoSection, gbc);

        // 功能区2: 宿舍服务
        gbc.gridx = 1;
        JPanel serviceSection = createHomeSectionPanel("宿舍服务", new Color(255, 245, 230));
        JButton btnToService = new JButton("申请服务");
        JButton btnToServiceStatus = new JButton("查看服务状态");
        btnToService.addActionListener(e -> cardLayout.show(cardPanel, CARD_SERVICE));
        btnToServiceStatus.addActionListener(e -> cardLayout.show(cardPanel, CARD_SERVICE_STATUS));
        
        JPanel serviceBtnPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        serviceBtnPanel.setOpaque(false);
        serviceBtnPanel.add(btnToService);
        serviceBtnPanel.add(btnToServiceStatus);
        serviceSection.add(serviceBtnPanel, BorderLayout.CENTER);
        panel.add(serviceSection, gbc);

        // 功能区3: 申请住宿
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JPanel applySection = createHomeSectionPanel("住宿申请", new Color(230, 255, 230));
        JButton btnToApplication = new JButton("申请入住/调换/退宿");
        btnToApplication.addActionListener(e -> cardLayout.show(cardPanel, CARD_APPLICATION));
        applySection.add(btnToApplication, BorderLayout.CENTER);
        panel.add(applySection, gbc);

        return panel;
    }

    private JPanel createHomeSectionPanel(String title, Color bgColor) {
        JPanel section = new JPanel(new BorderLayout(10, 10));
        section.setBackground(bgColor);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        section.setPreferredSize(new Dimension(250, 150));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setForeground(new Color(70, 70, 70));
        section.add(titleLabel, BorderLayout.NORTH);

        return section;
    }

    /**
     * 住宿信息显示页面
     */
    private JPanel createInfoDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("我的住宿信息", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        title.setForeground(new Color(0, 100, 200));
        panel.add(title, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // 添加信息字段
        String[][] infoFields = {
            {"学号:", "20250066"}, {"姓名:", "张四"},
            {"楼栋:", "桃八A"}, {"房间号:", "221"},
            {"床位号:", "1"}, {"入住日期:", "2025-09-01"},
            {"状态:", "在住"}
        };

        for (String[] field : infoFields) {
            JLabel label = new JLabel(field[0]);
            label.setFont(new Font("微软雅黑", Font.BOLD, 14));
            infoPanel.add(label);

            JLabel value = new JLabel(field[1]);
            value.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            value.setForeground(new Color(0, 100, 200));
            infoPanel.add(value);
        }

        panel.add(infoPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 住宿申请页面
     */
    private JPanel createApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("住宿申请", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        title.setForeground(new Color(0, 100, 200));
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 15);

        // 申请类型
        JLabel typeLabel = new JLabel("申请类型:");
        typeLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        formPanel.add(typeLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"入住", "调换", "退宿"});
        typeCombo.setPreferredSize(new Dimension(200, 25));
        formPanel.add(typeCombo, gbc);

        // 申请说明
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel descLabel = new JLabel("申请说明:");
        descLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        formPanel.add(descLabel, gbc);

        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(5, 30);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descArea), gbc);

        // 提交按钮
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitBtn = new JButton("提交申请");
        submitBtn.setBackground(new Color(100, 180, 255));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "申请提交成功！请等待审核。");
            cardLayout.show(cardPanel, CARD_HOME);
        });
        formPanel.add(submitBtn, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 申请情况查看页面
     */
    private JPanel createApplicationStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("我的申请记录", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        title.setForeground(new Color(0, 100, 200));
        panel.add(title, BorderLayout.NORTH);

        // 表头
        String[] columns = {"申请ID", "类型", "申请时间", "处理状态", "审核人"};
        Object[][] data = {
            {"1", "入住", "2025-09-01", "已批准", "张六"},
            {"2", "调换", "2025-09-05", "待审核", ""}
        };

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        // 设置表头样式
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 13));
        header.setBackground(new Color(200, 230, 255));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 宿舍服务页面
     */
    private JPanel createServicePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("宿舍服务申请", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        title.setForeground(new Color(0, 100, 200));
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 15);

        // 服务类型
        JLabel typeLabel = new JLabel("服务类型:");
        typeLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        formPanel.add(typeLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"设施报修", "其他问题"});
        typeCombo.setPreferredSize(new Dimension(200, 25));
        formPanel.add(typeCombo, gbc);

        // 问题描述
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel descLabel = new JLabel("问题描述:");
        descLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        formPanel.add(descLabel, gbc);

        gbc.gridx = 1;
        JTextArea descArea = new JTextArea(5, 30);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descArea), gbc);

        // 提交按钮
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitBtn = new JButton("提交申请");
        submitBtn.setBackground(new Color(100, 180, 255));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "服务申请提交成功！我们会尽快处理。");
            cardLayout.show(cardPanel, CARD_HOME);
        });
        formPanel.add(submitBtn, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 服务申请情况页面
     */
    private JPanel createServiceStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("服务处理进度", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        title.setForeground(new Color(0, 100, 200));
        panel.add(title, BorderLayout.NORTH);

        // 表头
        String[] columns = {"服务ID", "问题描述", "提交时间", "处理状态", "负责人"};
        Object[][] data = {
            {"1", "空调报修", "2025-09-10", "待处理", "张三"},
            {"2", "厕所漏水", "2025-09-08", "已完成", "李四"},
            {"3", "宿舍清理", "2025-09-05", "处理中", "王五"}
        };

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        // 设置表头样式
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 13));
        header.setBackground(new Color(200, 230, 255));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
