package com.seu.vcampus.client.view.panel.main;

import com.seu.vcampus.client.service.LoginService;
import com.seu.vcampus.client.service.UserService;
import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.common.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * 用户中心面板 - 根据用户角色展示不同信息和功能
 */
public class UserCenterPanel extends JPanel implements NavigatablePanel {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private LoginService loginService;

    // 当前用户
    private User currentUser;

    // 共用字段
    private JTextField cidField, tsidField, nameField, genderField,
            ageField, birthDateField, phoneField, emailField,
            addressField, idCardField, collegeField;

    // 学生特有
    private JTextField gradeField, studentTypeField, enrollmentDateField;

    // 教师特有
    private JTextField titleField, hireDateField;

    public UserCenterPanel() {
        currentUser = MainFrame.getInstance().getCurrentUser();
        loginService = new LoginService();
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // 左侧导航栏
        String[] options = {"个人信息", "账户管理"};
        JList<String> navList = new JList<>(options);
        navList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        navList.setSelectedIndex(0); // 默认选中“个人信息”
        navList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = navList.getSelectedValue();
                if ("个人信息".equals(selected)) {
                    cardLayout.show(contentPanel, "INFO");
                } else if ("账户管理".equals(selected)) {
                    cardLayout.show(contentPanel, "ACCOUNT");
                }
            }
        });

        JScrollPane navScrollPane = new JScrollPane(navList);
        navScrollPane.setPreferredSize(new Dimension(150, 0));

        add(navScrollPane, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // 初始化子面板
        contentPanel.add(createInfoPanel(), "INFO");
        contentPanel.add(createAccountPanel(), "ACCOUNT");
    }

    /**
     * 创建“个人信息”面板
     */
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 初始化所有字段
        initializeFields();

        int y = 0;

        // 添加通用信息
        addRow(panel, gbc, "一卡通号:", cidField, y++);
        addRow(panel, gbc, "姓名:", nameField, y++);
        addRow(panel, gbc, "性别:", genderField, y++);
        addRow(panel, gbc, "年龄:", ageField, y++);
        addRow(panel, gbc, "出生日期:", birthDateField, y++);
        addRow(panel, gbc, "电话号码:", phoneField, y++);
        addRow(panel, gbc, "电子邮箱:", emailField, y++);
        addRow(panel, gbc, "家庭地址:", addressField, y++);
        addRow(panel, gbc, "身份证号:", idCardField, y++);
        addRow(panel, gbc, "学院:", collegeField, y++);

        // 根据当前用户角色添加特有字段
        if (currentUser != null) {
            switch (currentUser.getRole()) {
                case "ST":
                    addRow(panel, gbc, "学号:", tsidField, y++);
                    addRow(panel, gbc, "入学时间:", enrollmentDateField, y++);
                    addRow(panel, gbc, "年级:", gradeField, y++);
                    addRow(panel, gbc, "学生类型:", studentTypeField, y++);
                    break;
                case "TC":
                    addRow(panel, gbc, "教职工号:", tsidField, y++);
                    addRow(panel, gbc, "入职时间:", hireDateField, y++);
                    addRow(panel, gbc, "职称:", titleField, y++);
                    break;
                case "AD":
                    // 管理员只显示部分字段，上面已包含
                    break;
            }
        }

        // 修改按钮下拉菜单
        JComboBox<String> editCombo = new JComboBox<>(new String[]{"修改...", "修改基本信息", "修改所有信息"});
        editCombo.addActionListener(e -> {
            String selection = (String) editCombo.getSelectedItem();
            if ("修改基本信息".equals(selection)) {
                enterBasicEditMode();
            } else if ("修改所有信息".equals(selection)) {
                enterFullEditMode();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(editCombo, gbc);

        return panel;
    }

    /**
     * 创建“账户管理”面板
     */
    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JButton changePasswordBtn = new JButton("修改密码");
        changePasswordBtn.addActionListener(e -> showChangePasswordDialog());

        JButton logoutBtn = new JButton("注销登录");
        logoutBtn.addActionListener(e -> performLogout());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(changePasswordBtn, gbc);

        gbc.gridy = 1;
        panel.add(logoutBtn, gbc);

        return panel;
    }

    /**
     * 添加一行 label + field
     */
    private void addRow(JPanel panel, GridBagConstraints gbc, String label, JComponent comp, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(comp, gbc);
    }

    /**
     * 初始化所有文本字段
     */
    private void initializeFields() {
        cidField = new JTextField(20);
        tsidField = new JTextField(20);
        nameField = new JTextField(20);
        genderField = new JTextField(20);
        ageField = new JTextField(20);
        birthDateField = new JTextField(20);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        addressField = new JTextField(20);
        idCardField = new JTextField(20);
        collegeField = new JTextField(20);

        gradeField = new JTextField(20);
        studentTypeField = new JTextField(20);
        enrollmentDateField = new JTextField(20);

        titleField = new JTextField(20);
        hireDateField = new JTextField(20);

        // 默认不可编辑
        setAllEditable(false);

        // 绑定身份证号变化事件
        idCardField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateFromIdCard(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateFromIdCard(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateFromIdCard(); }
        });
    }

    /**
     * 设置所有字段是否可编辑
     */
    private void setAllEditable(boolean editable) {
        cidField.setEditable(false); // 一卡通号不能改
        nameField.setEditable(false);
        genderField.setEditable(false);
        ageField.setEditable(false);
        birthDateField.setEditable(false);
        tsidField.setEditable(false); // 学号/工号不能直接改

        phoneField.setEditable(editable);
        emailField.setEditable(editable);
        addressField.setEditable(editable);
        idCardField.setEditable(editable);
        gradeField.setEditable(editable);
        studentTypeField.setEditable(editable);
        enrollmentDateField.setEditable(editable);
        titleField.setEditable(editable);
        hireDateField.setEditable(editable);
        collegeField.setEditable(editable);
    }

    /**
     * 进入“修改基本信息”模式
     */
    private void enterBasicEditMode() {
        setAllEditable(true);
        phoneField.setEditable(true);
        emailField.setEditable(true);
        addressField.setEditable(true);

        if ("ST".equals(currentUser.getRole())) {
            gradeField.setEditable(true);
        } else if ("TC".equals(currentUser.getRole())) {
            // 教师只有三项可基本修改
            titleField.setEditable(false);
            hireDateField.setEditable(false);
            collegeField.setEditable(false);
        }

        JOptionPane.showMessageDialog(this,
                "已进入【修改基本信息】模式\n可修改：电话、邮箱、地址" +
                        ("ST".equals(currentUser.getRole()) ? "、年级" : ""),
                "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 进入“修改所有信息”模式（需审批）
     */
    private void enterFullEditMode() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "您正在修改敏感信息（如身份证号），\n修改后需管理员审核通过才生效。\n是否继续？",
                "敏感信息修改", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        setAllEditable(true);
        JOptionPane.showMessageDialog(this,
                "已进入【修改所有信息】模式\n修改后需管理员审批才能生效。",
                "提示", JOptionPane.INFORMATION_MESSAGE);

        // TODO: 在这里可以记录原始值，用于提交审批请求
        // sendMessage(Message.TYPE_UPDATE_USER_PENDING, userDataMap);
    }

    /**
     * 从身份证号更新年龄和出生日期
     */
    private void updateFromIdCard() {
        String idCard = idCardField.getText().trim();
        if (idCard.length() == 18) {
            try {
                String birthStr = idCard.substring(6, 14); // YYYYMMDD
                LocalDate birth = LocalDate.parse(birthStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
                LocalDate now = LocalDate.now();
                int age = Period.between(birth, now).getYears();

                birthDateField.setText(birth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                ageField.setText(String.valueOf(age));
            } catch (Exception e) {
                // 解析失败不处理，保持原样
            }
        }
    }

    /**
     * 显示修改密码对话框（带验证码验证）
     */
    private void showChangePasswordDialog() {
        // 1. 第一步：输入原密码，获取验证码
        JPasswordField oldPf = new JPasswordField(10);
        JTextField emailOrPhoneField = new JTextField(20); // 假设显示用户邮箱或手机号
        JButton getCodeBtn = new JButton("获取验证码");
        JTextField verifyCodeField = new JTextField(10);

        // 假设从 currentUser 获取联系方式，这里优先使用邮箱
        String contact = currentUser.getEmail() != null && !currentUser.getEmail().isEmpty() ?
                currentUser.getEmail() : currentUser.getPhone();
        if (contact == null || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "无法获取您的联系方式，请联系管理员！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        emailOrPhoneField.setText(contact); // TODO 显示脱敏后的联系方式
        emailOrPhoneField.setEditable(false);

        // 记录验证码（实际项目中应由后端生成并存储，前端仅用于演示）
        final String[] storedCode = {""};
        final boolean[] codeSent = {false};

        // 设置获取验证码按钮逻辑
        getCodeBtn.addActionListener(e -> {
            // TODO: 调用后端接口发送验证码
            // 示例：ClientSocket.getInstance().sendMessage(Message.TYPE_SEND_VERIFY_CODE, Map.of("cid", currentUser.getCid()));

            // 模拟生成验证码（仅用于演示，生产环境由服务器生成）
//            String code = String.format("%06d", new java.util.Random().nextInt(1000000));
            String code = "123456";
            storedCode[0] = code;
            codeSent[0] = true;

            // 模拟发送成功提示
            JOptionPane.showMessageDialog(this, "验证码已发送至 " + contact, "提示", JOptionPane.INFORMATION_MESSAGE);

            // 禁用按钮60秒（防止重复点击）
            getCodeBtn.setEnabled(false);
            new Thread(() -> {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException ignored) {}
                SwingUtilities.invokeLater(() -> getCodeBtn.setEnabled(true));
            }).start();
        });

        Object[] step1 = {
                "原密码:", oldPf,
                "验证码将发送至:", emailOrPhoneField,
                "", getCodeBtn,
                "请输入验证码:", verifyCodeField
        };

        int option = JOptionPane.showConfirmDialog(this, step1, "修改密码 - 第一步", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return; // 用户取消
        }

        char[] oldPass = oldPf.getPassword();
        String inputCode = verifyCodeField.getText().trim();

        // 验证验证码
        if (!codeSent[0]) {
            JOptionPane.showMessageDialog(this, "请先获取验证码！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!inputCode.equals(storedCode[0])) {
            JOptionPane.showMessageDialog(this, "验证码错误或已过期！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. 第二步：输入新密码
        JPasswordField newPf = new JPasswordField(10);
        JPasswordField confirmPf = new JPasswordField(10);

        Object[] step2 = {
                "新密码:", newPf,
                "确认新密码:", confirmPf
        };

        option = JOptionPane.showConfirmDialog(this, step2, "修改密码 - 第二步", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        char[] newPass = newPf.getPassword();
        char[] confirmPass = confirmPf.getPassword();

        if (!new String(newPass).equals(new String(confirmPass))) {
            JOptionPane.showMessageDialog(this, "两次输入的新密码不一致！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (new String(newPass).length() < 6) {
            JOptionPane.showMessageDialog(this, "新密码长度不能少于6位！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // TODO: 发送修改密码请求到服务器（已通过验证码验证）
        // Map<String, Object> data = new HashMap<>();
        // data.put("cid", currentUser.getCid());
        // data.put("newPassword", new String(newPass));
        // data.put("verifyCode", inputCode); // 后端需校验此验证码
        // ClientSocket.getInstance().sendMessage(Message.TYPE_CHANGE_PASSWORD, data);

        // 模拟成功
        JOptionPane.showMessageDialog(this, "密码修改成功，请重新登录。", "成功", JOptionPane.INFORMATION_MESSAGE);

        // 可选：跳转到登录页
        performLogout(); // 注销当前会话
    }

    /**
     * 执行注销操作
     */
    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "确定要注销吗？", "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (loginService.logout(currentUser.getCid())) {
                currentUser = null;
                JOptionPane.showMessageDialog(this, "已注销，返回登录页。", "提示", JOptionPane.INFORMATION_MESSAGE);
                MainFrame.getInstance().showLoginPanel();
            } else {
                JOptionPane.showMessageDialog(this, "登出失败，请重试。", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @Override
    public void refreshPanel(User user) {
        this.currentUser = user;

        // 清空并重新初始化字段
        initializeFields();

        // 填充数据
        if (user != null) {
            cidField.setText(user.getCid());
            nameField.setText(user.getName());
            phoneField.setText(user.getPhone());
            emailField.setText(user.getEmail());
//            addressField.setText(user.getAddress());
//            idCardField.setText(user.getIdCard());
//            genderField.setText(user.getGender());
//            collegeField.setText(user.getCollege());

            // 角色相关数据填充
            if ("ST".equals(user.getRole())) {
                tsidField.setText(user.getTsid());
//                enrollmentDateField.setText(user.getEnrollmentDate());
//                gradeField.setText(user.getGrade());
//                studentTypeField.setText(user.getStudentType());
            } else if ("TC".equals(user.getRole())) {
                tsidField.setText(user.getTsid());
//                hireDateField.setText(user.getHireDate());
//                titleField.setText(user.getTitle());
            }
            // 更新年龄和生日
            updateFromIdCard();
        }

        // 刷新视图
        contentPanel.removeAll();
        contentPanel.add(createInfoPanel(), "INFO");
        contentPanel.add(createAccountPanel(), "ACCOUNT");
        cardLayout.show(contentPanel, "INFO"); // 默认显示信息页
    }

    @Override
    public String getPanelName() {
        return "USER_CENTER";
    }
}