package com.seu.vcampus.client.view.panel.login;

import com.seu.vcampus.client.service.UserService;
import com.seu.vcampus.common.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

public class ForgotPasswordDialog extends JDialog {
    private JTextField txtPhoneOrEmail;
    private JTextField txtVerificationCode;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnGetCode;
    private JButton btnConfirm;
    private JButton btnCancel;
    private JLabel lblCountdown;

    private int countdown = 60;
    private Timer countdownTimer;
    private static long lastSentTime = 0;
    private static int remainingCooldown = 0;

    private final UserService userService;

    // 用于切换“获取验证码”按钮和倒计时标签
    private JPanel buttonOrCountdownPanel;
    private CardLayout cardLayout;

    public ForgotPasswordDialog(Frame owner) {
        super(owner, "找回密码", true);
        userService = new UserService();
        initializeUI();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setSize(400, 350);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题
        JLabel lblTitle = new JLabel("找回密码", SwingConstants.CENTER);
        lblTitle.setFont(new Font("微软雅黑", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        add(lblTitle, gbc);

        // 手机/邮箱
        JLabel lblPhoneEmail = new JLabel("手机/邮箱:");
        lblPhoneEmail.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(lblPhoneEmail, gbc);

        txtPhoneOrEmail = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(txtPhoneOrEmail, gbc);

        // === 使用 CardLayout 切换按钮和倒计时 ===
        btnGetCode = new JButton("获取验证码");
        btnGetCode.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnGetCode.setPreferredSize(new Dimension(100, 25));

        lblCountdown = new JLabel("获取验证码 (60s)", SwingConstants.CENTER);
        lblCountdown.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lblCountdown.setForeground(Color.RED);
        lblCountdown.setPreferredSize(new Dimension(100, 25));
        lblCountdown.setOpaque(true);
        lblCountdown.setBackground(new Color(240, 240, 240));
        lblCountdown.setBorder(BorderFactory.createEtchedBorder());

        cardLayout = new CardLayout();
        buttonOrCountdownPanel = new JPanel(cardLayout);
        buttonOrCountdownPanel.add(btnGetCode, "GET_CODE");
        buttonOrCountdownPanel.add(lblCountdown, "COUNTDOWN");

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(buttonOrCountdownPanel, gbc);

        // 验证码
        JLabel lblVerificationCode = new JLabel("验证码:");
        lblVerificationCode.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(lblVerificationCode, gbc);

        txtVerificationCode = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(txtVerificationCode, gbc);

        // 新密码
        JLabel lblNewPassword = new JLabel("新密码:");
        lblNewPassword.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(lblNewPassword, gbc);

        txtNewPassword = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(txtNewPassword, gbc);

        // 确认密码
        JLabel lblConfirmPassword = new JLabel("确认密码:");
        lblConfirmPassword.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(lblConfirmPassword, gbc);

        txtConfirmPassword = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(txtConfirmPassword, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnConfirm = new JButton("确定");
        btnConfirm.setFont(new Font("微软雅黑", Font.BOLD, 12));
        btnConfirm.setPreferredSize(new Dimension(80, 30));

        btnCancel = new JButton("取消");
        btnCancel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnCancel.setPreferredSize(new Dimension(80, 30));

        buttonPanel.add(btnConfirm);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        add(buttonPanel, gbc);

        // 添加事件监听
        btnGetCode.addActionListener(e -> getVerificationCode());
        btnConfirm.addActionListener(e -> resetPassword());
        btnCancel.addActionListener(e -> dispose());

        // === 检查是否需要恢复倒计时状态 ===
        long currentTime = System.currentTimeMillis();
        long timeSinceLastSend = currentTime - lastSentTime;
        long cooldown = 60 * 1000;

        if (timeSinceLastSend < cooldown) {
            // 仍在冷却期内，恢复倒计时
            int secondsLeft = (int) ((cooldown - timeSinceLastSend) / 1000);
            remainingCooldown = Math.max(1, secondsLeft); // 确保至少1秒
            SwingUtilities.invokeLater(() -> {
                startCountdownWithRemaining(remainingCooldown);
            });
        }
    }

    private void getVerificationCode() {
        String phoneOrEmail = txtPhoneOrEmail.getText().trim();
        if (phoneOrEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入手机号或邮箱", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long currentTime = System.currentTimeMillis();
        long timeSinceLastSend = currentTime - lastSentTime;
        long cooldown = 60 * 1000;

        if (timeSinceLastSend < cooldown) {
            int secondsLeft = (int) ((cooldown - timeSinceLastSend) / 1000);
            JOptionPane.showMessageDialog(this, "请等待 " + secondsLeft + " 秒", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 生成验证码（服务端生成并保存）
        userService.generateVerificationCode(phoneOrEmail);

        JOptionPane.showMessageDialog(this, "验证码已发送到: " + phoneOrEmail, "提示", JOptionPane.INFORMATION_MESSAGE);

        lastSentTime = currentTime;
        remainingCooldown = 60;
        startCountdown();
    }

    private void startCountdown() {
        btnGetCode.setEnabled(false); // 防止重复点击
        cardLayout.show(buttonOrCountdownPanel, "COUNTDOWN"); // 显示倒计时
        countdown = 60;

        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    countdown--;
                    lblCountdown.setText("获取验证码" + " (" + countdown + "s)");

                    if (countdown <= 0) {
                        stopCountdown();
                    }
                });
            }
        }, 1000, 1000);
    }

    private void stopCountdown() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }
        cardLayout.show(buttonOrCountdownPanel, "GET_CODE");
        btnGetCode.setEnabled(true);
        remainingCooldown = 0;
    }

    private void startCountdownWithRemaining(int seconds) {
        countdown = seconds;
        btnGetCode.setEnabled(false);
        cardLayout.show(buttonOrCountdownPanel, "COUNTDOWN");
        lblCountdown.setText("获取验证码(" + countdown + "s)");

        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    countdown--;
                    if (countdown > 0) {
                        lblCountdown.setText("获取验证码(" + countdown + "s)");
                    } else {
                        stopCountdown();
                    }
                });
            }
        }, 1000, 1000);
    }

    private void resetPassword() {
        String phoneOrEmail = txtPhoneOrEmail.getText().trim();
        String verificationCode = txtVerificationCode.getText().trim();
        String newPassword = new String(txtNewPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        // 验证输入
        if (phoneOrEmail.isEmpty() || verificationCode.isEmpty() ||
                newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写所有字段", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "两次输入的密码不一致", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!userService.verifyCode(phoneOrEmail, verificationCode)) {
            JOptionPane.showMessageDialog(this, "验证码错误或已过期", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = new User();
        // 判断是邮箱还是手机号
        if (phoneOrEmail.contains("@")) {
            user = userService.getUserByEmail(phoneOrEmail);
        } else {
            user = userService.getUserByPhone(phoneOrEmail);
        }

        if (user != null) {
            user.setPassword(newPassword);
            if (userService.updateUser(user)) {
                JOptionPane.showMessageDialog(this, "密码修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "服务器异常，密码修改失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "用户不存在，请检查手机号或邮箱", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void dispose() {
        stopCountdown(); // 停止倒计时并恢复按钮
        super.dispose();
    }
}