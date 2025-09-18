package com.seu.vcampus.client.view.panel.student;

import com.seu.vcampus.client.service.StudentService;
import com.seu.vcampus.common.model.Student;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;



public class ADInsertPanel extends JFrame {
    private JPanel mainPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private final StudentService studentService = new StudentService();
    private final String[] columnNames = new String[] {"姓名","电话","邮箱","性别","年龄","出生日期","家庭住址","身份证号","入学日期","年级","专业","学籍号","学制","学籍状态"};

    public ADInsertPanel() {
        // 设置窗口属性
        setTitle("添加学生");
        setSize(800, 600);
        setLocationRelativeTo(null); // 居中显示

        // 创建主面板，使用BorderLayout
        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // 创建左侧导航栏
        JPanel sidebarPanel = createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // 创建右侧内容区域，使用CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // 添加内容面板
        contentPanel.add(createDashboardPanel(), "Dashboard");
        contentPanel.add(createSettingsPanel(), "Settings");

        // 默认显示第一个面板
        cardLayout.show(contentPanel, "Dashboard");
    }

    // 创建左侧导航栏
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(150, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // 创建按钮
        JButton dashboardBtn = createSidebarButton("手动添加");
        JButton settingsBtn = createSidebarButton("从文件导入");

        // 添加按钮点击事件
        dashboardBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "Dashboard");
            }
        });

        settingsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "Settings");
            }
        });

        // 添加按钮到侧边栏
        sidebar.add(dashboardBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10))); // 按钮间距
        sidebar.add(settingsBtn);
        sidebar.add(Box.createVerticalGlue()); // 将按钮推到顶部

        return sidebar;
    }

    // 创建侧边栏按钮样式
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setMaximumSize(new Dimension(120, 40));
        button.setMargin(new Insets(3, 8, 3, 8));
        return button;
    }

    // 创建控制面板内容
    private JPanel createDashboardPanel() {
        // 创建面板放置筛选条件
        JPanel filterPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Map<Integer, JTextField> dialogFilterFields = new HashMap<>();

        for (int i = 0; i < columnNames.length; i++) {
            JLabel label = new JLabel(columnNames[i] + ":");
            JTextField textField = new JTextField();

            dialogFilterFields.put(i, textField);
            filterPanel.add(label);
            filterPanel.add(textField);
        }

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("确定");

        okButton.addActionListener(e -> {
            List<Student> newStudents = new ArrayList<>();
            for(Map.Entry<Integer, JTextField> entry : dialogFilterFields.entrySet()) {
                if(entry.getValue().getText().isEmpty()) {
                    JDialog dialog = new JDialog(this, "error", true);
                    dialog.setSize(300, 200);
                    dialog.setLocationRelativeTo(this);
                    JButton closeButton = new JButton("关闭");
                    closeButton.setFocusPainted(false);
                    closeButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dialog.dispose(); // 关闭对话框
                        }
                    });
                    JLabel label = new JLabel("存在信息为空", SwingConstants.CENTER);
                    dialog.setLayout(new BorderLayout());
                    dialog.add(label, BorderLayout.CENTER);
                    dialog.add(closeButton, BorderLayout.SOUTH);
                    dialog.setVisible(true);
                    return;
                }
            }
            newStudents.add(new Student(dialogFilterFields));
            addStudent(newStudents);
        });
        buttonPanel.add(okButton);
        filterPanel.add(buttonPanel);

        return filterPanel;
    }

    public void addStudent(List<Student> studentList) {
        try {
            studentService.addStudent(studentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 创建系统设置内容
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建设置表单
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 50, 50));

        formPanel.add(new JLabel("用户名:"));
        formPanel.add(new JTextField());
        formPanel.add(new JLabel("邮箱:"));
        formPanel.add(new JTextField());
        formPanel.add(new JLabel("主题:"));

        JComboBox<String> themeCombo = new JComboBox<>(new String[]{"浅色主题", "深色主题"});
        formPanel.add(themeCombo);

        formPanel.add(new JLabel("通知:"));
        JCheckBox notifyCheck = new JCheckBox("启用通知");
        formPanel.add(notifyCheck);

        panel.add(formPanel, BorderLayout.CENTER);

        // 添加保存按钮
        JButton saveButton = new JButton("保存设置");
        saveButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }


    public static void main(String[] args) {
        // 使用SwingUtilities确保GUI线程安全
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ADInsertPanel().setVisible(true);
            }
        });
    }
}