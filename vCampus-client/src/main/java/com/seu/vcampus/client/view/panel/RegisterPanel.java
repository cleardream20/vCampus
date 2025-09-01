// File: src/main/java/com/seu/vcampus/client/view/panel/RegisterPanel.java

package com.seu.vcampus.client.view.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import com.seu.vcampus.client.controller.LoginController;
import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.common.model.User;

public class RegisterPanel extends JPanel implements NavigatablePanel {
    private JTextField txtCid;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtName;
    private JComboBox<String> cmbGender;
    private JButton btnRegister;
    private JButton btnCancel;
    private JLabel lblStatus;

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
        txtCid = new JTextField(15);  // ✅ 先创建并赋值
        addLabeledField("一卡通号:", txtCid, gbc, 1, 0, 1);  // 再添加到界面

        // 姓名
        txtName = new JTextField(15);
        addLabeledField("姓　名:", txtName, gbc, 2, 0, 1);

        // 密码
        txtPassword = new JPasswordField(15);
        addLabeledField("密　码:", txtPassword, gbc, 4, 0, 1);

        // 确认密码
        txtConfirmPassword = new JPasswordField(15);
        addLabeledField("确认密码:", txtConfirmPassword, gbc, 5, 0, 1);

        // 性别
        JLabel lblGender = new JLabel("性　别:");
        lblGender.setFont(DEFAULT_FONT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(lblGender, gbc);

        cmbGender = new JComboBox<>(new String[]{"男", "女"});
        cmbGender.setFont(DEFAULT_FONT);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        add(cmbGender, gbc);

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
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // 状态提示
        lblStatus = new JLabel("请填写注册信息", SwingConstants.CENTER);
        lblStatus.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        lblStatus.setForeground(Color.GRAY);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        add(lblStatus, gbc);

        // 事件监听
        btnRegister.addActionListener(e -> attemptRegister());
        btnCancel.addActionListener(e -> cancelRegister());

        // 回车注册
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

    private void attemptRegister() {
        String cid = txtCid.getText().trim();
        String name = txtName.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String gender = (String) cmbGender.getSelectedItem();

        // 校验
        if (cid.isEmpty() || name.isEmpty() || password.isEmpty()) {
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

        lblStatus.setText("注册中...");
        lblStatus.setForeground(Color.BLUE);

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                LoginController controller = new LoginController();
//                boolean success = controller.register(cid, name, gender, password);
                boolean success = true;
                if (success) {
                    // 注册成功后，尝试自动登录
                    return controller.login(cid, password);
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

                    // 自动登录并跳转主界面
                    SwingUtilities.invokeLater(() -> {
                        MainFrame mainFrame = MainFrame.getInstance();
                        mainFrame.showMainPanel(user);
                        mainFrame.showLibraryPanel(); // 默认进入图书馆模块
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
        // 返回登录界面
        SwingUtilities.invokeLater(() -> {
            MainFrame.getInstance().showLoginPanel();
        });
    }

    @Override
    public void refreshPanel(User user) {
        // 清空表单
        txtCid.setText("");
        txtName.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        cmbGender.setSelectedIndex(0);
        lblStatus.setText("请填写注册信息");
        lblStatus.setForeground(Color.GRAY);
    }

    @Override
    public String getPanelName() {
        return "REGISTER";
    }
}