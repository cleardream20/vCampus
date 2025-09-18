package com.seu.vcampus.client.view.panel.main;

import com.seu.vcampus.client.service.LoginService;
import com.seu.vcampus.client.service.UserService;
import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.client.view.frame.MainFrame;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * 用户中心面板 - 根据用户角色展示不同信息和功能
 */
public class UserCenterPanel extends JPanel implements NavigatablePanel {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private LoginService loginService;
    private UserService userService;

    // 当前用户
    private User currentUser;

    // 共用字段
    private JTextField cidField, tsidField, nameField, genderField,
            ageField, birthDateField, phoneField, emailField,
            addressField, idCardField, collegeField;

    // 学生特有
    private JTextField gradeField, studentTypeField, endateField;

    // 教师特有
    private JTextField titleField, hireDateField;

    // 修改按钮
    private JButton editButton;
    private boolean isEditMode = false;

    public UserCenterPanel() {
        currentUser = MainFrame.getInstance().getCurrentUser();
        loginService = new LoginService();
        userService = new UserService();
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // 左侧导航栏
        String[] options = {"个人信息", "账户管理"};
        JList<String> navList = new JList<>(options);
        navList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        navList.setSelectedIndex(0);
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

        // 新增：顶部工具栏（放置返回按钮）
        JPanel topToolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backToMainBtn = new JButton("返回主界面");
        backToMainBtn.addActionListener(this::handleReturnAction);
        topToolbar.add(backToMainBtn);

        // 使用嵌套面板：左侧导航 + 右侧（顶部工具栏 + 内容）
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(topToolbar, BorderLayout.NORTH);
        rightPanel.add(contentPanel, BorderLayout.CENTER);

        // 添加到主面板
        add(navScrollPane, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER); // 右侧整体加入

        // 初始化子面板
        contentPanel.add(createInfoPanel(), "INFO");
        contentPanel.add(createAccountPanel(), "ACCOUNT");
    }

    /**
     * 导航到主界面（图书浏览页）
     */
    private void handleReturnAction(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            MainFrame.getInstance().showMainPanel(currentUser);
        });
    }

    /**
     * 创建"个人信息"面板
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
                    addRow(panel, gbc, "入学时间:", endateField, y++);
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

        // 修改/确定/取消按钮
        editButton = new JButton("修改");
        JButton cancelButton = new JButton("取消");
        cancelButton.setVisible(false);

        editButton.addActionListener(e -> toggleEditMode());
        cancelButton.addActionListener(e -> cancelEdit());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(editButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    /**
     * 切换编辑模式
     */
    private void toggleEditMode() {
        if (!isEditMode) {
            // 进入编辑模式
            setAllEditable(true);
            editButton.setText("确定");
            isEditMode = true;
            // 显示取消按钮
            ((JButton) ((JPanel) editButton.getParent()).getComponent(1)).setVisible(true);
        } else {
            // 保存修改
            saveChanges();
        }
    }

    /**
     * 取消编辑
     */
    private void cancelEdit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要取消修改吗？所有未保存的更改将丢失。",
                "确认取消", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // 刷新数据，恢复原状
            refreshPanel(currentUser);
            setEditMode(false);
        }
    }

    /**
     * 设置编辑模式状态
     */
    private void setEditMode(boolean editMode) {
        isEditMode = editMode;
        editButton.setText(editMode ? "确定" : "修改");
        setAllEditable(editMode);
        // 隐藏取消按钮
        ((JButton) ((JPanel) editButton.getParent()).getComponent(1)).setVisible(editMode);
    }

    /**
     * 保存修改
     */
    private void saveChanges() {
        // 收集修改后的数据
        if (currentUser != null) {
            currentUser.setPhone(phoneField.getText());
            currentUser.setEmail(emailField.getText());

            // 如果是学生，更新学生信息
            if ("ST".equals(currentUser.getRole())) {
                Student student = MainFrame.getInstance().getCurrentStudent();
                if (student != null) {
                    student.setAddress(addressField.getText());
                    student.setNid(idCardField.getText());
                    student.setGrade(gradeField.getText());
                    student.setEs(studentTypeField.getText());
                    student.setEndate(endateField.getText());
                    // 更新当前学生对象
                    MainFrame.getInstance().setCurrentStudent(student);
                }
            }

            // 调用服务更新用户信息
            if (userService.updateUser(currentUser)) {
                JOptionPane.showMessageDialog(this, "信息更新成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                setEditMode(false);
            } else {
                JOptionPane.showMessageDialog(this, "信息更新失败，请重试！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 创建"账户管理"面板
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
        endateField = new JTextField(20);

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
        tsidField.setEditable(false); // 学号/工号不能改
        nameField.setEditable(false);
        genderField.setEditable(false);
        ageField.setEditable(false);
        birthDateField.setEditable(false);

        // 可编辑的字段
        phoneField.setEditable(editable);
        emailField.setEditable(editable);
        addressField.setEditable(editable);
        idCardField.setEditable(editable);
        collegeField.setEditable(editable);

        // 角色特有字段
        if ("ST".equals(currentUser.getRole())) {
            gradeField.setEditable(editable);
            studentTypeField.setEditable(editable);
            endateField.setEditable(editable);
        } else if ("TC".equals(currentUser.getRole())) {
            titleField.setEditable(editable);
            hireDateField.setEditable(editable);
        }
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
        JTextField emailOrPhoneField = new JTextField(20);
        JButton getCodeBtn = new JButton("获取验证码");
        JTextField verifyCodeField = new JTextField(10);

        String contact = currentUser.getEmail() != null && !currentUser.getEmail().isEmpty() ?
                currentUser.getEmail() : currentUser.getPhone();
        if (contact == null || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "无法获取您的联系方式，请联系管理员！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        emailOrPhoneField.setText(contact);
        emailOrPhoneField.setEditable(false);

        final String[] storedCode = {"123456"}; // 模拟验证码
        final boolean[] codeSent = {false};

        getCodeBtn.addActionListener(e -> {
            storedCode[0] = "123456";
            codeSent[0] = true;
            JOptionPane.showMessageDialog(this, "验证码已发送至 " + contact, "提示", JOptionPane.INFORMATION_MESSAGE);
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
            return;
        }

        char[] oldPass = oldPf.getPassword();
        String inputCode = verifyCodeField.getText().trim();

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

        User user = MainFrame.getInstance().getCurrentUser();
        user.setPassword(new String(confirmPass));
        if (userService.updateUser(user)) {
            JOptionPane.showMessageDialog(this, "密码修改成功，请重新登录。", "成功", JOptionPane.INFORMATION_MESSAGE);
            performLogout();
        } else {
            JOptionPane.showMessageDialog(this, "密码修改失败，请重试。", "失败", JOptionPane.INFORMATION_MESSAGE);
        }
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

            // 角色相关数据填充
            if ("ST".equals(user.getRole())) {
                Student st = MainFrame.getInstance().getCurrentStudent();
                if (st != null) {
                    tsidField.setText(user.getTsid());
                    endateField.setText(st.getEndate());
                    addressField.setText(st.getAddress());
                    idCardField.setText(st.getNid());
                    gradeField.setText(st.getGrade());
                    studentTypeField.setText(st.getEs());
                    // 设置性别和学院（根据你的实际字段名调整）
                    genderField.setText(st.getSex());
                    collegeField.setText(st.getMajor()); // 假设major对应学院
                }
            } else if ("TC".equals(user.getRole())) {
                tsidField.setText(user.getTsid());
                // 教师信息填充（根据你的实际字段名调整）
            }
            // 更新年龄和生日
            updateFromIdCard();
        }

        // 重置编辑模式
        setEditMode(false);

        // 刷新视图
        contentPanel.removeAll();
        contentPanel.add(createInfoPanel(), "INFO");
        contentPanel.add(createAccountPanel(), "ACCOUNT");
        cardLayout.show(contentPanel, "INFO");
    }

    @Override
    public String getPanelName() {
        return "USER_CENTER";
    }
}