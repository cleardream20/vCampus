package com.seu.vcampus.client.view.panel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class DormAdminPanel extends JPanel {

    private CardLayout cardLayout;
    private JPanel cardPanel;

    // 管理端卡片名称常量
    private static final String CARD_HOME = "ADMIN_HOME";
    private static final String CARD_DORM_MGMT = "DORM_MGMT";
    private static final String CARD_INFO_VIEW = "INFO_VIEW";
    private static final String CARD_INFO_EDIT = "INFO_EDIT";
    private static final String CARD_APPROVAL = "APPROVAL";
    private static final String CARD_SERVICE_MGMT = "SERVICE_MGMT";
    private static final String CARD_REPAIR_MGMT = "REPAIR_MGMT";
    private static final String CARD_COMPLAINT_MGMT = "COMPLAINT_MGMT";

    public DormAdminPanel() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建顶部导航栏（管理端使用不同的颜色主题）
        JPanel navPanel = createAdminNavPanel();
        add(navPanel, BorderLayout.NORTH);

        // 创建主卡片面板
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // 创建并添加所有管理端子页面
        cardPanel.add(createAdminHomePanel(), CARD_HOME);
        cardPanel.add(createDormMgmtPanel(), CARD_DORM_MGMT);
        cardPanel.add(createInfoViewPanel(), CARD_INFO_VIEW);
        cardPanel.add(createInfoEditPanel(), CARD_INFO_EDIT);
        cardPanel.add(createApprovalPanel(), CARD_APPROVAL);
        cardPanel.add(createServiceMgmtPanel(), CARD_SERVICE_MGMT);
        cardPanel.add(createRepairMgmtPanel(), CARD_REPAIR_MGMT);
        cardPanel.add(createComplaintMgmtPanel(), CARD_COMPLAINT_MGMT);

        // 默认显示管理端首页
        cardLayout.show(cardPanel, CARD_HOME);
        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createAdminNavPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        panel.setBackground(new Color(255, 240, 245)); // 浅红色背景区分管理端

        JButton btnHome = new JButton("管理首页");
        JButton btnBack = new JButton("返回");

        btnHome.setBackground(new Color(255, 220, 230));
        btnBack.setBackground(new Color(255, 220, 230));
        btnHome.setFocusPainted(false);
        btnBack.setFocusPainted(false);

        btnHome.addActionListener(e -> cardLayout.show(cardPanel, CARD_HOME));
        btnBack.addActionListener(e -> cardLayout.show(cardPanel, CARD_HOME));

        panel.add(btnHome);
        panel.add(btnBack);
        return panel;
    }

    /**
     * 1. 管理端首页
     */
    private JPanel createAdminHomePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 住宿管理功能区
        JPanel dormSection = createAdminSectionPanel("住宿管理", new Color(255, 230, 230));
        JButton btnToDormMgmt = new JButton("进入");
        btnToDormMgmt.addActionListener(e -> cardLayout.show(cardPanel, CARD_DORM_MGMT));
        dormSection.add(btnToDormMgmt);
        panel.add(dormSection);

        // 服务管理功能区
        JPanel serviceSection = createAdminSectionPanel("服务管理", new Color(230, 230, 255));
        JButton btnToServiceMgmt = new JButton("进入");
        btnToServiceMgmt.addActionListener(e -> cardLayout.show(cardPanel, CARD_SERVICE_MGMT));
        serviceSection.add(btnToServiceMgmt);
        panel.add(serviceSection);

        return panel;
    }

    private JPanel createAdminSectionPanel(String title, Color bgColor) {
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
     * 2. 住宿管理页面
     */
    private JPanel createDormMgmtPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnInfoView = createAdminButton("住宿信息查看", "查看所有学生的住宿信息");
        JButton btnInfoEdit = createAdminButton("住宿信息修改", "修改学生住宿信息");
        JButton btnApproval = createAdminButton("申请审核", "审核学生的住宿申请");

        btnInfoView.addActionListener(e -> cardLayout.show(cardPanel, CARD_INFO_VIEW));
        btnInfoEdit.addActionListener(e -> cardLayout.show(cardPanel, CARD_INFO_EDIT));
        btnApproval.addActionListener(e -> cardLayout.show(cardPanel, CARD_APPROVAL));

        panel.add(btnInfoView);
        panel.add(btnInfoEdit);
        panel.add(btnApproval);

        return panel;
    }

    private JButton createAdminButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(200, 45));
        button.setBackground(new Color(255, 240, 245));
        return button;
    }

    /**
     * 住宿信息查看页面
     */
    private JPanel createInfoViewPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 搜索栏
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("搜索学生:"));
        searchPanel.add(new JTextField(15));
        JButton searchBtn = new JButton("搜索");
        searchPanel.add(searchBtn);

        panel.add(searchPanel, BorderLayout.NORTH);

        // 学生住宿信息表格
        String[] columns = {"学号", "姓名", "楼栋", "房间号", "床位号", "状态"};
        String[][] data = {
            {"123456789", "张三", "紫荆1号楼", "101A", "1", "在住"},
            {"123456790", "李四", "紫荆1号楼", "101A", "2", "在住"},
            {"123456791", "王五", "紫荆2号楼", "203B", "3", "在住"},
            {"123456792", "赵六", "紫荆3号楼", "305C", "1", "已退宿"}
        };

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 住宿信息修改页面
     */
    private JPanel createInfoEditPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("住宿信息修改", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("学号:"));
        formPanel.add(new JTextField());
        
        formPanel.add(new JLabel("楼栋:"));
        formPanel.add(new JTextField());
        
        formPanel.add(new JLabel("房间号:"));
        formPanel.add(new JTextField());
        
        formPanel.add(new JLabel("床位号:"));
        formPanel.add(new JTextField());
        
        formPanel.add(new JLabel("状态:"));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"在住", "已退宿", "预留"});
        formPanel.add(statusCombo);

        panel.add(formPanel, BorderLayout.CENTER);

        JButton saveBtn = new JButton("保存修改");
        saveBtn.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, "信息更新成功！"));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 申请审核页面 - 简化版本，不使用表格按钮
     */
    private JPanel createApprovalPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("住宿申请审核", JLabel.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"申请ID", "学号", "姓名", "申请类型", "申请时间", "状态"};
        Object[][] data = {
            {"10087", "123456789", "张三", "调换", "2023-10-11 09:15", "待审核"},
            {"10089", "123456790", "李四", "入住", "2024-02-21 14:20", "待审核"}
        };

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 添加操作按钮面板
        JPanel buttonPanel = new JPanel();
        JButton approveBtn = new JButton("批准");
        JButton rejectBtn = new JButton("拒绝");
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);

        // 添加按钮事件
        approveBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String applicationId = (String) table.getValueAt(selectedRow, 0);
                JOptionPane.showMessageDialog(panel, "已批准申请: " + applicationId);
                // 更新表格状态
                table.setValueAt("已批准", selectedRow, 5);
            } else {
                JOptionPane.showMessageDialog(panel, "请先选择一行");
            }
        });

        rejectBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String applicationId = (String) table.getValueAt(selectedRow, 0);
                JOptionPane.showMessageDialog(panel, "已拒绝申请: " + applicationId);
                table.setValueAt("已拒绝", selectedRow, 5);
            } else {
                JOptionPane.showMessageDialog(panel, "请先选择一行");
            }
        });

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 3. 服务管理页面
     */
    private JPanel createServiceMgmtPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnRepairMgmt = createAdminButton("报修处理", "处理学生报修请求");
        JButton btnComplaintMgmt = createAdminButton("投诉建议处理", "处理学生投诉和建议");

        btnRepairMgmt.addActionListener(e -> cardLayout.show(cardPanel, CARD_REPAIR_MGMT));
        btnComplaintMgmt.addActionListener(e -> cardLayout.show(cardPanel, CARD_COMPLAINT_MGMT));

        panel.add(btnRepairMgmt);
        panel.add(btnComplaintMgmt);

        return panel;
    }

    /**
     * 报修处理页面 - 简化版本，不使用表格按钮
     */
    private JPanel createRepairMgmtPanel() {
        return createServiceMgmtSubPanel("报修处理", new String[]{
            "RP1001", "紫荆1号楼101A", "水管漏水", "2024-02-20 09:30", "处理中"
        });
    }

    /**
     * 投诉建议处理页面 - 简化版本，不使用表格按钮
     */
    private JPanel createComplaintMgmtPanel() {
        return createServiceMgmtSubPanel("投诉与建议处理", new String[]{
            "CP1001", "紫荆2号楼203B", "隔壁宿舍太吵闹", "2024-02-19 16:40", "待处理"
        });
    }

    private JPanel createServiceMgmtSubPanel(String title, String[] exampleData) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"服务ID", "位置", "问题描述", "提交时间", "状态"};
        String[][] data = {exampleData, 
            {"RP1002", "紫荆3号楼305C", "空调不制冷", "2024-02-18 14:25", "已完成"}
        };

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 添加操作按钮
        JPanel buttonPanel = new JPanel();
        JButton processBtn = new JButton("处理");
        buttonPanel.add(processBtn);

        processBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String serviceId = (String) table.getValueAt(selectedRow, 0);
                JOptionPane.showMessageDialog(panel, "开始处理: " + serviceId);
                table.setValueAt("处理中", selectedRow, 4);
            } else {
                JOptionPane.showMessageDialog(panel, "请先选择一行");
            }
        });

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
}
