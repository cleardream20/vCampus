package com.seu.vcampus.client.view.frame;

import com.seu.vcampus.client.view.panel.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JButton adminLoginButton;
    private JTabbedPane userTabbedPane;

    public MainFrame() {
        setTitle("vCampus 选课系统");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 使用CardLayout实现界面切换
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 创建用户界面卡片
        mainPanel.add(createUserPanel(), "user");
        // 创建管理员界面卡片
        mainPanel.add(createAdminPanel(), "admin");

        add(mainPanel);
        showUserPanel();
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 顶部选项卡（与图片2完全一致）
        userTabbedPane = new JTabbedPane();
        userTabbedPane.addTab("课程查询", new CourseQueryPanel());
        userTabbedPane.addTab("选课管理", new CourseSelectionPanel());
        userTabbedPane.addTab("我的课表", new CourseSchedulePanel());

        // 右上角管理员登录按钮
        adminLoginButton = new JButton("管理员登录");
        adminLoginButton.setPreferredSize(new Dimension(100, 25));
        adminLoginButton.addActionListener(e -> showAdminLoginDialog());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(userTabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(adminLoginButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 顶部返回按钮（与图片1一致）
        JButton backButton = new JButton("返回用户界面");
        backButton.addActionListener(e -> showUserPanel());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(backButton);
        panel.add(topPanel, BorderLayout.NORTH);

        // 管理员功能选项卡（与图片1一致）
        JTabbedPane adminTabbedPane = new JTabbedPane();
        adminTabbedPane.addTab("课程管理", new CourseManagementPanel());
        adminTabbedPane.addTab("选课统计", new SelectionReportPanel());

        panel.add(adminTabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private void showAdminLoginDialog() {
        JDialog loginDialog = new JDialog(this, "管理员登录", true);
        loginDialog.setSize(400, 250);
        loginDialog.setLayout(new GridBagLayout());
        loginDialog.getContentPane().setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 标题（居中）
        JLabel titleLabel = new JLabel("管理员登录");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        loginDialog.add(titleLabel, gbc);

        // 管理员ID输入
        JLabel idLabel = new JLabel("管理员ID:");
        idLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginDialog.add(idLabel, gbc);

        JTextField idField = new JTextField(15);
        idField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginDialog.add(idField, gbc);

        // 密码输入
        JLabel pwLabel = new JLabel("密码:");
        pwLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        loginDialog.add(pwLabel, gbc);

        JPasswordField pwField = new JPasswordField(15);
        pwField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginDialog.add(pwField, gbc);

        // 登录按钮
        JButton loginBtn = new JButton("登录");
        loginBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        loginBtn.setPreferredSize(new Dimension(100, 35));
        loginBtn.setBackground(new Color(100, 149, 237));
        loginBtn.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginDialog.add(loginBtn, gbc);

        // 登录事件
        loginBtn.addActionListener(e -> {
            if (authenticate(idField.getText(), new String(pwField.getPassword()))) {
                loginDialog.dispose();
                adminLoginButton.setText("返回用户");
                showAdminPanel();
            } else {
                JOptionPane.showMessageDialog(loginDialog,
                        "管理员ID或密码错误",
                        "登录失败",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        loginDialog.setLocationRelativeTo(this);
        loginDialog.setVisible(true);
    }

    private boolean authenticate(String id, String password) {
        return "admin".equals(id) && "admin123".equals(password);
    }

    public void showUserPanel() {
        cardLayout.show(mainPanel, "user");
        adminLoginButton.setText("管理员登录");
    }

    public void showAdminPanel() {
        cardLayout.show(mainPanel, "admin");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}