package com.seu.vcampus.client.view.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import com.seu.vcampus.client.controller.LoginController;
import com.seu.vcampus.client.service.LoginService;
import com.seu.vcampus.client.service.UserService;
import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.common.model.User;

public class RegisterPanel extends JPanel implements NavigatablePanel {
    private JTextField txtCid;
    private JTextField txtTsid;
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JLabel lblRole;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtVerificationCode; // 新增验证码输入框
    private JButton btnRegister;
    private JButton btnCancel;
    private JLabel lblStatus;

    // 验证码相关组件
    private JButton btnGetEmailCode;
    private JButton btnGetPhoneCode;
    private JLabel lblEmailCountdown;
    private JLabel lblPhoneCountdown;
    private JPanel emailButtonPanel;
    private JPanel phoneButtonPanel;
    private CardLayout emailCardLayout;
    private CardLayout phoneCardLayout;

    // 验证码计时器相关
    private Timer countdownTimer;
    private int countdown = 60;
    private static long lastSentTime = 0;
    private static int remainingCooldown = 0;

    private static final int COLS = 25;
    private static final Font DEFAULT_FONT = new Font("微软雅黑", Font.PLAIN, 14);

    private final UserService userService;

    public RegisterPanel() {
        userService = new UserService();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题
        JLabel lblTitle = new JLabel("用户注册", SwingConstants.CENTER);
        lblTitle.setFont(new Font("微软雅黑", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // 先初始化lblRole
        lblRole = new JLabel("未 知");
        lblRole.setFont(DEFAULT_FONT);
        lblRole.setForeground(Color.GRAY);

        // 一卡通号
        txtCid = new JTextField(15);
        txtCid.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { updateDerivedFields(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { updateDerivedFields(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { updateDerivedFields(); }
        });
        addLabeledField("一卡通号:", txtCid, gbc, 1, 0, 1);

        // 其他组件初始化...
        // 学号/教职工号
        txtTsid = new JTextField(COLS);
        addLabeledField("学号/工号:", txtTsid, gbc, 2, 0, 1);

        // 姓名
        txtName = new JTextField(COLS);
        addLabeledField("姓　名:", txtName, gbc, 3, 0, 1);

        // 邮箱（带验证码按钮）
        JPanel emailPanel = new JPanel(new BorderLayout(5, 0));
        txtEmail = new JTextField(COLS - 5);
        emailPanel.add(txtEmail, BorderLayout.CENTER);

        // 邮箱验证码按钮面板
        btnGetEmailCode = new JButton("获取验证码");
        btnGetEmailCode.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnGetEmailCode.setPreferredSize(new Dimension(100, 25));

        lblEmailCountdown = new JLabel("60秒", SwingConstants.CENTER);
        lblEmailCountdown.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lblEmailCountdown.setForeground(Color.RED);
        lblEmailCountdown.setPreferredSize(new Dimension(100, 25));
        lblEmailCountdown.setOpaque(true);
        lblEmailCountdown.setBackground(new Color(240, 240, 240));
        lblEmailCountdown.setBorder(BorderFactory.createEtchedBorder());

        emailCardLayout = new CardLayout();
        emailButtonPanel = new JPanel(emailCardLayout);
        emailButtonPanel.setPreferredSize(new Dimension(100, 25));
        emailButtonPanel.add(btnGetEmailCode, "BUTTON");
        emailButtonPanel.add(lblEmailCountdown, "COUNTDOWN");
        emailPanel.add(emailButtonPanel, BorderLayout.EAST);

        addLabeledComponent("邮　箱:", emailPanel, gbc, 4, 0, 1);

        // 电话（带验证码按钮）
        JPanel phonePanel = new JPanel(new BorderLayout(5, 0));
        txtPhone = new JTextField(COLS - 5);
        phonePanel.add(txtPhone, BorderLayout.CENTER);

        // 电话验证码按钮面板
        btnGetPhoneCode = new JButton("获取验证码");
        btnGetPhoneCode.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btnGetPhoneCode.setPreferredSize(new Dimension(100, 25));

        lblPhoneCountdown = new JLabel("60秒", SwingConstants.CENTER);
        lblPhoneCountdown.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lblPhoneCountdown.setForeground(Color.RED);
        lblPhoneCountdown.setPreferredSize(new Dimension(100, 25));
        lblPhoneCountdown.setOpaque(true);
        lblPhoneCountdown.setBackground(new Color(240, 240, 240));
        lblPhoneCountdown.setBorder(BorderFactory.createEtchedBorder());

        phoneCardLayout = new CardLayout();
        phoneButtonPanel = new JPanel(phoneCardLayout);
        phoneButtonPanel.setPreferredSize(new Dimension(100, 25));
        phoneButtonPanel.add(btnGetPhoneCode, "BUTTON");
        phoneButtonPanel.add(lblPhoneCountdown, "COUNTDOWN");
        phonePanel.add(phoneButtonPanel, BorderLayout.EAST);

        addLabeledComponent("电　话:", phonePanel, gbc, 5, 0, 1);

        // 身份（只读） - 这里使用已经初始化的lblRole
        JLabel lblIdentity = new JLabel("身　份:");
        lblIdentity.setFont(DEFAULT_FONT);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(lblIdentity, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        add(lblRole, gbc); // 使用已经初始化的lblRole

        // 验证码输入
        txtVerificationCode = new JTextField(COLS);
        addLabeledField("验证码:", txtVerificationCode, gbc, 7, 0, 1);

        // 密码
        txtPassword = new JPasswordField(COLS);
        addLabeledField("密　码:", txtPassword, gbc, 8, 0, 1);

        // 确认密码
        txtConfirmPassword = new JPasswordField(COLS);
        addLabeledField("确认密码:", txtConfirmPassword, gbc, 9, 0, 1);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnRegister = new JButton("注册");
        btnRegister.setFont(new Font("微软雅黑", Font.BOLD, 14));
        btnRegister.setPreferredSize(new Dimension(90, 30));

        btnCancel = new JButton("取消");
        btnCancel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        btnCancel.setPreferredSize(new Dimension(90, 30));

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // 状态提示
        lblStatus = new JLabel("请填写注册信息", SwingConstants.CENTER);
        lblStatus.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lblStatus.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        add(lblStatus, gbc);

        // 事件监听
        btnRegister.addActionListener(e -> attemptRegister());
        btnCancel.addActionListener(e -> cancelRegister());
        btnGetEmailCode.addActionListener(e -> getVerificationCode("email"));
        btnGetPhoneCode.addActionListener(e -> getVerificationCode("phone"));

        txtConfirmPassword.addActionListener(e -> attemptRegister());

        // 检查是否需要恢复倒计时状态
        checkAndRestoreCountdown();
    }

    private void addLabeledComponent(String labelText, JComponent component, GridBagConstraints gbc, int row, int labelCol, int fieldCol) {
        JLabel label = new JLabel(labelText);
        label.setFont(DEFAULT_FONT);
        gbc.gridx = labelCol;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(label, gbc);

        gbc.gridx = fieldCol;
        gbc.anchor = GridBagConstraints.WEST;
        add(component, gbc);
    }

    private void addLabeledField(String labelText, JComponent field, GridBagConstraints gbc, int row, int labelCol, int fieldCol) {
        addLabeledComponent(labelText, field, gbc, row, labelCol, fieldCol);
    }

    private void getVerificationCode(String type) {
        String contact = "";
        if ("email".equals(type)) {
            contact = txtEmail.getText().trim();
            if (contact.isEmpty() || !contact.matches("\\S+@\\S+\\.\\S+")) {
                JOptionPane.showMessageDialog(this, "请输入有效的邮箱地址", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else if ("phone".equals(type)) {
            contact = txtPhone.getText().trim();
            if (contact.isEmpty() || !contact.matches("\\d{11}")) {
                JOptionPane.showMessageDialog(this, "请输入有效的11位手机号", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        long currentTime = System.currentTimeMillis();
        long timeSinceLastSend = currentTime - lastSentTime;
        long cooldown = 60 * 1000;

        if (timeSinceLastSend < cooldown) {
            int secondsLeft = (int) ((cooldown - timeSinceLastSend) / 1000);
            JOptionPane.showMessageDialog(this, "请等待 " + secondsLeft + " 秒后再获取验证码", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 生成验证码（服务端生成并保存）
        String code = userService.generateVerificationCode(contact);
        if (code != null && !code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "验证码已发送到: " + contact, "提示", JOptionPane.INFORMATION_MESSAGE);
            lastSentTime = currentTime;
            remainingCooldown = 60;
            startCountdown();
        } else {
            JOptionPane.showMessageDialog(this, "验证码发送失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startCountdown() {
        // 禁用两个验证码按钮
        btnGetEmailCode.setEnabled(false);
        btnGetPhoneCode.setEnabled(false);

        // 显示倒计时
        emailCardLayout.show(emailButtonPanel, "COUNTDOWN");
        phoneCardLayout.show(phoneButtonPanel, "COUNTDOWN");

        countdown = 60;

        if (countdownTimer != null) {
            countdownTimer.cancel();
        }

        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    countdown--;
                    lblEmailCountdown.setText(countdown + "s");
                    lblPhoneCountdown.setText(countdown + "s");

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

        // 恢复按钮状态
        emailCardLayout.show(emailButtonPanel, "BUTTON");
        phoneCardLayout.show(phoneButtonPanel, "BUTTON");
        btnGetEmailCode.setEnabled(true);
        btnGetPhoneCode.setEnabled(true);
        remainingCooldown = 0;
    }

    private void checkAndRestoreCountdown() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastSend = currentTime - lastSentTime;
        long cooldown = 60 * 1000;

        if (timeSinceLastSend < cooldown) {
            int secondsLeft = (int) ((cooldown - timeSinceLastSend) / 1000);
            remainingCooldown = Math.max(1, secondsLeft);
            SwingUtilities.invokeLater(() -> {
                startCountdownWithRemaining(remainingCooldown);
            });
        }
    }

    private void startCountdownWithRemaining(int seconds) {
        countdown = seconds;

        btnGetEmailCode.setEnabled(false);
        btnGetPhoneCode.setEnabled(false);
        emailCardLayout.show(emailButtonPanel, "COUNTDOWN");
        phoneCardLayout.show(phoneButtonPanel, "COUNTDOWN");

        lblEmailCountdown.setText(countdown + "s");
        lblPhoneCountdown.setText(countdown + "s");

        if (countdownTimer != null) {
            countdownTimer.cancel();
        }

        countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    countdown--;
                    if (countdown > 0) {
                        lblEmailCountdown.setText(countdown + "s");
                        lblPhoneCountdown.setText(countdown + "s");
                    } else {
                        stopCountdown();
                    }
                });
            }
        }, 1000, 1000);
    }

    private void updateDerivedFields() {
        // 原有实现保持不变
        String cid = txtCid.getText().trim();
        if (cid.length() < 2) {
            lblRole.setText("未 知");
            lblRole.setForeground(Color.GRAY);
            return;
        }

        String prefix = cid.substring(0, 2);
        String role = "";

        switch (prefix) {
            case "21": role = "学生"; break;
            case "10": role = "教师"; break;
            case "00": role = "管理员"; break;
            default: role = "无效身份"; break;
        }

        lblRole.setText(role);
        Color color = switch (role) {
            case "学生" -> new Color(0, 100, 200);
            case "教师" -> new Color(0, 150, 0);
            case "管理员" -> new Color(200, 100, 0);
            default -> Color.RED;
        };
        lblRole.setForeground(color);
    }

    private void attemptRegister() {
        String cid = txtCid.getText().trim();
        String tsid = txtTsid.getText().trim();
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String verificationCode = txtVerificationCode.getText().trim(); // 获取验证码
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        // 基本校验（增加验证码校验）
        if (cid.isEmpty() || tsid.isEmpty() || name.isEmpty() ||
                email.isEmpty() || phone.isEmpty() || verificationCode.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
            lblStatus.setText("所有字段均为必填");
            lblStatus.setForeground(Color.RED);
            return;
        }

        // 验证码校验
        if (!userService.verifyCode(email, verificationCode) &&
                !userService.verifyCode(phone, verificationCode)) {
            lblStatus.setText("验证码错误或已过期");
            lblStatus.setForeground(Color.RED);
            return;
        }

        if (!password.equals(confirmPassword)) {
            lblStatus.setText("两次密码输入不一致");
            lblStatus.setForeground(Color.RED);
            return;
        }
        if (password.length() < 6) {
            lblStatus.setText("密码至少6位");
            lblStatus.setForeground(Color.RED);
            return;
        }
        if (!email.matches("\\S+@\\S+\\.\\S+")) {
            lblStatus.setText("邮箱格式不正确");
            lblStatus.setForeground(Color.RED);
            return;
        }

        lblStatus.setText("注册中...");
        lblStatus.setForeground(Color.BLUE);

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                LoginService loginService = new LoginService();

                String roleStr = lblRole.getText();
                String roleCode = switch (roleStr) {
                    case "学生" -> "ST";
                    case "教师" -> "TC";
                    case "管理员" -> "AD";
                    default -> throw new IllegalArgumentException("非法身份：" + roleStr);
                };

                User user = new User();
                user.setCid(cid);
                user.setTsid(tsid);
                user.setName(name);
                user.setEmail(email);
                user.setPhone(phone);
                user.setPassword(password);
                user.setRole(roleCode);

                boolean success = loginService.register(user);
                if (success) {
                    return loginService.login(cid, password);
                } else {
                    throw new Exception("注册失败：账号已存在或服务器错误");
                }
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    lblStatus.setText("注册成功！");
                    lblStatus.setForeground(Color.GREEN);

                    SwingUtilities.invokeLater(() -> {
                        MainFrame mainFrame = MainFrame.getInstance();
                        mainFrame.showMainPanel(user);
                    });

                } catch (InterruptedException | ExecutionException ex) {
                    lblStatus.setText("注册失败: " + ex.getMessage());
                    lblStatus.setForeground(Color.RED);
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void cancelRegister() {
        SwingUtilities.invokeLater(() -> {
            MainFrame.getInstance().showLoginPanel();
        });
    }

    @Override
    public void refreshPanel(User user) {
        txtCid.setText("");
        txtTsid.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtVerificationCode.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        lblRole.setText("待输入一卡通号");
        lblRole.setForeground(Color.GRAY);
        lblStatus.setText("请填写注册信息");
        lblStatus.setForeground(Color.GRAY);

        // 停止倒计时
        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }

        // 恢复按钮状态
        emailCardLayout.show(emailButtonPanel, "BUTTON");
        phoneCardLayout.show(phoneButtonPanel, "BUTTON");
        btnGetEmailCode.setEnabled(true);
        btnGetPhoneCode.setEnabled(true);
    }

    @Override
    public String getPanelName() {
        return "REGISTER";
    }
}