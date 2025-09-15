// File: src/main/java/com/seu/vcampus/client/view/panel/RegisterPanel.java

package com.seu.vcampus.client.view.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import com.seu.vcampus.client.controller.LoginController;
import com.seu.vcampus.client.service.LoginService;
import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.common.model.User;

public class RegisterPanel extends JPanel implements NavigatablePanel {
    private JTextField txtCid;
    private JTextField txtTsid;        // 学号/工号
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JLabel lblRole;            // 显示身份（只读）
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JLabel lblStatus;

    private static final int COLS = 25;
    private static final Font DEFAULT_FONT = new Font("微软雅黑", Font.PLAIN, 14);

    public RegisterPanel() {
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

        // 一卡通号
        txtCid = new JTextField(15);
        txtCid.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { updateDerivedFields(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { updateDerivedFields(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { updateDerivedFields(); }
        });
        addLabeledField("一卡通号:", txtCid, gbc, 1, 0, 1);

        // 学号/教职工号
        txtTsid = new JTextField(COLS);
//        txtTsid.setEditable(false);
//        txtTsid.setBackground(Color.LIGHT_GRAY);
        addLabeledField("学号/工号:", txtTsid, gbc, 2, 0, 1);

        // 姓名
        txtName = new JTextField(COLS);
        addLabeledField("姓　名:", txtName, gbc, 3, 0, 1);

        // 邮箱
        txtEmail = new JTextField(COLS);
        addLabeledField("邮　箱:", txtEmail, gbc, 4, 0, 1);

        // 电话
        txtPhone = new JTextField(COLS);
        addLabeledField("电　话:", txtPhone, gbc, 5, 0, 1);

        // 身份（只读）
        JLabel lblIdentity = new JLabel("身　份:");
        lblIdentity.setFont(DEFAULT_FONT);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(lblIdentity, gbc);

        lblRole = new JLabel("未 知");
        lblRole.setFont(DEFAULT_FONT);
        lblRole.setForeground(Color.GRAY);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        add(lblRole, gbc);

        // 密码
        txtPassword = new JPasswordField(COLS);
        addLabeledField("密　码:", txtPassword, gbc, 7, 0, 1);

        // 确认密码
        txtConfirmPassword = new JPasswordField(COLS);
        addLabeledField("确认密码:", txtConfirmPassword, gbc, 8, 0, 1);

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
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // 状态提示
        lblStatus = new JLabel("请填写注册信息", SwingConstants.CENTER);
        lblStatus.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lblStatus.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        add(lblStatus, gbc);

        // 事件监听
        btnRegister.addActionListener(e -> attemptRegister());
        btnCancel.addActionListener(e -> cancelRegister());

        // 回车确认密码时注册
        txtConfirmPassword.addActionListener(e -> attemptRegister());
    }

    // 辅助方法：快速添加标签+输入框
    private void addLabeledField(String labelText, JComponent field, GridBagConstraints gbc, int row, int labelCol, int fieldCol) {
        JLabel label = new JLabel(labelText);
        label.setFont(DEFAULT_FONT);
        gbc.gridx = labelCol;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(label, gbc);

        gbc.gridx = fieldCol;
        gbc.anchor = GridBagConstraints.WEST;
        add(field, gbc);
    }

    /**
     * 当一卡通号变化时，自动更新 tsid 和 role
     */
    private void updateDerivedFields() {
        String cid = txtCid.getText().trim();
        if (cid.length() < 2) {
//            txtTsid.setText("");
            lblRole.setText("未 知");
            lblRole.setForeground(Color.GRAY);
            return;
        }

        String prefix = cid.substring(0, 2);
//        String tsid = "";
        String role = "";

        switch (prefix) {
            case "21":
                role = "学生";
                break;
            case "10":
                role = "教师";
                break;
            case "00":
                role = "管理员";
                break;
            default:
                role = "无效身份";
                break;
        }

        lblRole.setText(role);

        // 根据身份设置颜色
        Color color = switch (role) {
            case "学生" -> new Color(0, 100, 200);     // 蓝色
            case "教师" -> new Color(0, 150, 0);       // 绿色
            case "管理员" -> new Color(200, 100, 0);   // 橙色
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
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        // 基本校验
        if (cid.isEmpty() || tsid.isEmpty() || name.isEmpty() ||
                email.isEmpty() || phone.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty()) {
            lblStatus.setText("所有字段均为必填");
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
//        if (!phone.matches("\\d{11}")) {
//            lblStatus.setText("电话应为11位数字");
//            lblStatus.setForeground(Color.RED);
//            return;
//        }

        lblStatus.setText("注册中...");
        lblStatus.setForeground(Color.BLUE);

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                LoginService loginService = new LoginService();

                // 构造 User 对象
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
                    // 注册成功后尝试登录
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

                    // 跳转主界面
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
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        lblRole.setText("待输入一卡通号");
        lblRole.setForeground(Color.GRAY);
        lblStatus.setText("请填写注册信息");
        lblStatus.setForeground(Color.GRAY);
    }

    @Override
    public String getPanelName() {
        return "REGISTER";
    }
}