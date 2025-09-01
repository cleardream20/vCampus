package com.seu.vcampus.client.view.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import com.seu.vcampus.client.controller.LoginController;
import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.common.model.User;

public class LoginPanel extends JPanel implements NavigatablePanel {
    private JTextField txtCid;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private JLabel lblStatus;
    private JLabel lblTitle;

    // 字体常量（可选优化）
    private static final Font DEFAULT_FONT = new Font("微软雅黑", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("微软雅黑", Font.BOLD, 18);

    public LoginPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题
        lblTitle = new JLabel("虚拟校园系统登录", SwingConstants.CENTER);
        lblTitle.setFont(TITLE_FONT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // 一卡通号标签
        JLabel lblCid = new JLabel("一卡通号:");
        lblCid.setFont(DEFAULT_FONT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(lblCid, gbc);

        // 一卡通号输入框
        txtCid = new JTextField(15);
        txtCid.setFont(DEFAULT_FONT);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(txtCid, gbc);

        // 密码标签
        JLabel lblPassword = new JLabel("密码:");
        lblPassword.setFont(DEFAULT_FONT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(lblPassword, gbc);

        // 密码输入框
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(DEFAULT_FONT);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(txtPassword, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnLogin = new JButton("登录");
        btnLogin.setFont(new Font("微软雅黑", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(100, 35));

        btnRegister = new JButton("注册");
        btnRegister.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        btnRegister.setPreferredSize(new Dimension(100, 35));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegister);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // 状态标签
        lblStatus = new JLabel("请输入一卡通号和密码", SwingConstants.CENTER);
        lblStatus.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lblStatus.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(lblStatus, gbc);

        // 添加事件监听
        btnLogin.addActionListener(e -> attemptLogin());
        btnRegister.addActionListener(e -> attemptRegister());

        // 回车触发登录
        txtPassword.addActionListener(e -> attemptLogin());
        txtCid.addActionListener(e -> attemptLogin()); // 可选：一卡通号回车也登录
    }

    private void attemptLogin() {
        String cid = txtCid.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (cid.isEmpty() || password.isEmpty()) {
            lblStatus.setText("一卡通号和密码不能为空");
            lblStatus.setForeground(Color.RED);
            return;
        }

        lblStatus.setText("登录中...");
        lblStatus.setForeground(Color.BLUE);

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                LoginController controller = new LoginController();
                User user = controller.login(cid, password);
                if (user != null) {
                    return user;
                } else {
                    throw new Exception("登录失败：账号或密码错误");
                }
            }

            @Override
            protected void done() {
                try {
                    User user = get(); // 获取登录返回的 User 对象
                    lblStatus.setText("登录成功");
                    lblStatus.setForeground(Color.GREEN);

                    // 登录成功：使用单例 MainFrame，切换到主界面
                    SwingUtilities.invokeLater(() -> {
                        MainFrame mainFrame = MainFrame.getInstance();
                        mainFrame.showMainPanel(user); // 设置当前用户
                        // mainFrame.show...()
                    });

                } catch (InterruptedException | ExecutionException ex) {
                    lblStatus.setText("登录失败: " + ex.getMessage());
                    lblStatus.setForeground(Color.RED);
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void attemptRegister() {
//        JOptionPane.showMessageDialog(this, "注册功能正在开发中", "提示", JOptionPane.INFORMATION_MESSAGE);
        SwingUtilities.invokeLater(() -> {
            MainFrame.getInstance().showPanel("REGISTER");
        });
    }

    @Override
    public void refreshPanel(User user) {
        // 清空登录表单
        txtCid.setText("");
        txtPassword.setText("");
        lblStatus.setText("请输入一卡通号和密码");
        lblStatus.setForeground(Color.GRAY);
    }

    @Override
    public String getPanelName() {
        return "LOGIN";
    }
}