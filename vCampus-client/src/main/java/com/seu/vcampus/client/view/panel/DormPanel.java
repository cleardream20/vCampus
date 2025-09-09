package com.seu.vcampus.client.view.panel;

import javax.swing.*;
import java.awt.*;

public class DormPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // 用户端卡片名称常量
    private static final String CARD_HOME = "HOME";
    private static final String CARD_INFO_MGMT = "INFO_MGMT";
    private static final String CARD_INFO_DISPLAY = "INFO_DISPLAY";
    private static final String CARD_APPLICATION_STATUS = "APP_STATUS";
    private static final String CARD_SERVICE = "SERVICE";
    private static final String CARD_REPAIR = "REPAIR";
    private static final String CARD_COMPLAINT = "COMPLAINT";
    private static final String CARD_SERVICE_STATUS = "SERVICE_STATUS";

    public DormPanel() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. 创建顶部导航栏
        JPanel navPanel = createNavPanel();
        add(navPanel, BorderLayout.NORTH);

        // 2. 创建主卡片面板
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 3. 创建并添加所有子页面
        cardPanel.add(createHomePanel(), CARD_HOME);
        cardPanel.add(createInfoMgmtPanel(), CARD_INFO_MGMT);
        cardPanel.add(createInfoDisplayPanel(), CARD_INFO_DISPLAY);
        cardPanel.add(createApplicationStatusPanel(), CARD_APPLICATION_STATUS);
        cardPanel.add(createServicePanel(), CARD_SERVICE);
        cardPanel.add(createRepairPanel(), CARD_REPAIR);
        cardPanel.add(createComplaintPanel(), CARD_COMPLAINT);
        cardPanel.add(createServiceStatusPanel(), CARD_SERVICE_STATUS);

        // 4. 默认显示首页
        cardLayout.show(cardPanel, CARD_HOME);
        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createNavPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        JButton btnHome = createNavButton("首页", new Color(220, 240, 255));
        JButton btnBack = createNavButton("返回", new Color(255, 240, 220));

        btnHome.addActionListener(e -> cardLayout.show(cardPanel, CARD_HOME));
        btnBack.addActionListener(e -> cardLayout.show(cardPanel, CARD_HOME));

        panel.add(btnHome);
        panel.add(btnBack);
        return panel;
    }

    private JButton createNavButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        return button;
    }

    /**
     * 1. 我的宿舍首页
     */
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 功能区1: 住宿信息与管理
        JPanel infoSection = createHomeSectionPanel("住宿信息与管理", new Color(230, 245, 255));
        JButton btnToInfoMgmt = new JButton("进入");
        btnToInfoMgmt.addActionListener(e -> cardLayout.show(cardPanel, CARD_INFO_MGMT));
        infoSection.add(btnToInfoMgmt);
        panel.add(infoSection);

        // 功能区2: 宿舍服务
        JPanel serviceSection = createHomeSectionPanel("宿舍服务", new Color(255, 245, 230));
        JButton btnToService = new JButton("进入");
        btnToService.addActionListener(e -> cardLayout.show(cardPanel, CARD_SERVICE));
        serviceSection.add(btnToService);
        panel.add(serviceSection);

        return panel;
    }

    private JPanel createHomeSectionPanel(String title, Color bgColor) {
        JPanel section = new JPanel(new BorderLayout(10, 10));
        section.setBackground(bgColor);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        section.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        section.add(buttonPanel, BorderLayout.CENTER);

        return section;
    }

    /**
     * 2. 住宿信息与管理页面
     */
    private JPanel createInfoMgmtPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnInfoDisplay = createMgmtButton("查看住宿信息", "显示您的详细住宿信息");
        JButton btnApplyCheckIn = createMgmtButton("申请入住", "提交新的入住申请");
        JButton btnApplyCheckOut = createMgmtButton("申请退宿", "提交退宿申请");
        JButton btnApplyAdjust = createMgmtButton("申请调整住宿", "申请更换宿舍或床位");
        JButton btnViewApplications = createMgmtButton("查看申请情况", "查看所有申请的审核状态");

        btnInfoDisplay.addActionListener(e -> cardLayout.show(cardPanel, CARD_INFO_DISPLAY));
        btnApplyCheckIn.addActionListener(e -> showApplicationDialog("入住"));
        btnApplyCheckOut.addActionListener(e -> showApplicationDialog("退宿"));
        btnApplyAdjust.addActionListener(e -> showApplicationDialog("调整"));
        btnViewApplications.addActionListener(e -> cardLayout.show(cardPanel, CARD_APPLICATION_STATUS));

        panel.add(btnInfoDisplay);
        panel.add(btnApplyCheckIn);
        panel.add(btnApplyCheckOut);
        panel.add(btnApplyAdjust);
        panel.add(btnViewApplications);

        return panel;
    }

    private JButton createMgmtButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }

    private void showApplicationDialog(String type) {
        JDialog dialog = new JDialog((Frame) null, "申请" + type, true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("申请类型: " + type + "住宿"), BorderLayout.NORTH);
        panel.add(new JTextArea("请在此填写申请理由...", 5, 30), BorderLayout.CENTER);

        JButton submitBtn = new JButton("提交申请");
        submitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "申请提交成功！请等待审核。");
            dialog.dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * 住宿信息显示页面
     */
    private JPanel createInfoDisplayPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 15);

        JLabel title = new JLabel("我的住宿信息详情");
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        gbc.gridwidth = 2;
        panel.add(title, gbc);

        String[][] infoData = {
            {"学号:", "123456789"}, {"姓名:", "张三"},
            {"学院:", "计算机科学与技术"}, {"专业:", "软件工程"},
            {"楼栋:", "紫荆1号楼"}, {"房间号:", "101A"},
            {"床位号:", "1"}, {"宿舍类型:", "4人间"},
            {"入住日期:", "2023-09-01"}, {"预计退宿日期:", "2027-06-30"},
            {"住宿状态:", "在住"}, {"宿舍电话:", "010-12345678"},
            {"舍友:", "李四, 王五, 赵六"}, {"宿管老师:", "陈老师"},
            {"老师电话:", "010-87654321"}
        };

        gbc.gridwidth = 1;
        gbc.gridy++;

        for (String[] data : infoData) {
            gbc.gridx = 0;
            panel.add(new JLabel(data[0], JLabel.RIGHT), gbc);

            gbc.gridx = 1;
            JLabel value = new JLabel(data[1]);
            value.setForeground(new Color(0, 100, 200));
            panel.add(value, gbc);

            gbc.gridy++;
        }

        return panel;
    }

    /**
     * 申请情况查看页面
     */
    private JPanel createApplicationStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("我的申请记录", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"申请ID", "类型", "申请时间", "处理状态", "审核人", "备注"};
        String[][] data = {
            {"10086", "入住", "2023-08-25 14:30", "已批准", "张管理员", "欢迎入住！"},
            {"10087", "调换", "2023-10-11 09:15", "审核中", "-", "希望与同班同学同住"},
            {"10088", "退宿", "2024-01-15 16:45", "已拒绝", "李管理员", "实习申请未通过审批"}
        };

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 3. 宿舍服务页面
     */
    private JPanel createServicePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnRepair = createServiceButton("设施报修", "报告宿舍设施故障");
        JButton btnComplaint = createServiceButton("投诉与建议", "提出投诉或建议");
        JButton btnServiceStatus = createServiceButton("查看服务进度", "查看报修和投诉的处理进度");

        btnRepair.addActionListener(e -> cardLayout.show(cardPanel, CARD_REPAIR));
        btnComplaint.addActionListener(e -> cardLayout.show(cardPanel, CARD_COMPLAINT));
        btnServiceStatus.addActionListener(e -> cardLayout.show(cardPanel, CARD_SERVICE_STATUS));

        panel.add(btnRepair);
        panel.add(btnComplaint);
        panel.add(btnServiceStatus);

        return panel;
    }

    private JButton createServiceButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(200, 50));
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        return button;
    }

    // 以下是各个服务子页面的实现（报修、投诉、服务进度）
    private JPanel createRepairPanel() {
        return createServiceSubPanel("设施报修", "请描述需要报修的设施和问题详情:");
    }

    private JPanel createComplaintPanel() {
        return createServiceSubPanel("投诉与建议", "请详细描述您的投诉内容或建议:");
    }

    private JPanel createServiceSubPanel(String title, String prompt) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        
        formPanel.add(new JLabel(prompt), BorderLayout.NORTH);
        formPanel.add(new JScrollPane(new JTextArea(8, 40)), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton submitBtn = new JButton("提交");
        submitBtn.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "提交成功！我们会尽快处理。"));
        
        buttonPanel.add(submitBtn);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createServiceStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("服务处理进度", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"服务ID", "类型", "提交时间", "处理状态", "负责人", "预计完成时间"};
        String[][] data = {
            {"S1001", "报修", "2024-02-20 09:30", "处理中", "维修部-张师傅", "2024-02-22"},
            {"S1002", "报修", "2024-02-18 14:25", "已完成", "维修部-李师傅", "2024-02-19"},
            {"S1003", "投诉", "2024-02-19 16:40", "待处理", "-", "-"}
        };

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
