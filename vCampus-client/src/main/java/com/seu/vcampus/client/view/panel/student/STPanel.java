package com.seu.vcampus.client.view.panel.student;

import javax.swing.*;
import java.awt.*;

import com.seu.vcampus.client.service.StudentService;
import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.model.User;

public class STPanel extends JPanel implements NavigatablePanel {
    private Student currentStudent;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField sexField;
    private JTextField birthdayField;
    private JTextField addressField;
    private JTextField nidField;
    private JTextField endateField;
    private JButton backButton; // 保留返回按钮

    public STPanel() {
//        User user = MainFrame.getInstance().getCurrentUser();
        User user = new User();
        String userId = user.getCid();
        StudentService service = new StudentService();
        try {
            this.currentStudent = service.getStudent(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeUI();
        setFieldsEditable(false); // 确保字段不可编辑
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 添加返回按钮到左上角
        backButton = new JButton("返回");
        backButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        backButton.setFocusPainted(false);
        backButton.setMargin(new Insets(2, 5, 2, 5));

        // 创建顶部面板，包含返回按钮和标题
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backButton, BorderLayout.WEST);

        // 标题
        JLabel titleLabel = new JLabel("学生个人信息", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // 添加顶部面板到主面板
        add(topPanel, BorderLayout.NORTH);

        // 创建内容面板，用于放置表单
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0)); // 添加顶部空白，实现下移效果

        // 表单面板 - 使用GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // 组件间距
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 第一行: 姓名
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("姓名:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(currentStudent.getName(), 20);
        formPanel.add(nameField, gbc);

        // 第二行: 邮箱
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("邮箱:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(currentStudent.getEmail(), 20);
        formPanel.add(emailField, gbc);

        // 第三行: 电话
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("电话:"), gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(currentStudent.getPhone(), 20);
        formPanel.add(phoneField, gbc);

        // 第四行: 性别
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("性别:"), gbc);

        gbc.gridx = 1;
        sexField = new JTextField(currentStudent.getSex(), 20);
        formPanel.add(sexField, gbc);

        // 第五行: 出生日期
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("出生日期:"), gbc);

        gbc.gridx = 1;
        birthdayField = new JTextField(currentStudent.getBirthday(), 20);
        formPanel.add(birthdayField, gbc);

        // 第六行: 地址
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("地址:"), gbc);

        gbc.gridx = 1;
        addressField = new JTextField(currentStudent.getAddress(), 20);
        formPanel.add(addressField, gbc);

        // 第七行: 身份证号
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("身份证号:"), gbc);

        gbc.gridx = 1;
        nidField = new JTextField(currentStudent.getNid(), 20);
        formPanel.add(nidField, gbc);

        // 第八行: 入学日期
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("入学日期:"), gbc);

        gbc.gridx = 1;
        endateField = new JTextField(currentStudent.getEndate(), 20);
        formPanel.add(endateField, gbc);

        // 添加空白组件以推动左侧标签右对齐
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        formPanel.add(Box.createGlue(), gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        // 将内容面板添加到主面板
        add(contentPanel, BorderLayout.CENTER);

        // 添加事件监听
        setupEventListeners();
    }

    private void setupEventListeners() {
        // 只保留返回按钮的事件监听
        backButton.addActionListener(e -> {
            // 这里实现返回逻辑，例如返回到上一个界面
            // 可能需要调用主框架的导航方法
            System.out.println("返回按钮被点击");
        });
    }

    private void setFieldsEditable(boolean editable) {
        nameField.setEditable(editable);
        emailField.setEditable(editable);
        phoneField.setEditable(editable);
        sexField.setEditable(editable);
        birthdayField.setEditable(editable);
        addressField.setEditable(editable);
        nidField.setEditable(editable);
        endateField.setEditable(editable);

        // 更改背景色以提示编辑状态
        Color bgColor = editable ? Color.WHITE : new Color(240, 240, 240);
        nameField.setBackground(bgColor);
        emailField.setBackground(bgColor);
        phoneField.setBackground(bgColor);
        sexField.setBackground(bgColor);
        birthdayField.setBackground(bgColor);
        addressField.setBackground(bgColor);
        nidField.setBackground(bgColor);
        endateField.setBackground(bgColor);
    }

    @Override
    public String getPanelName() {
        return "StudentST";
    }

    @Override
    public void refreshPanel(User user) {
        String userId = user.getCid();
        StudentService service = new StudentService();
        Student student = this.currentStudent;
        try {
            student = service.getStudent(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.currentStudent = student;

        // 更新所有字段
        nameField.setText(student.getName());
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhone());
        sexField.setText(student.getSex());
        birthdayField.setText(student.getBirthday());
        addressField.setText(student.getAddress());
        nidField.setText(student.getNid());
        endateField.setText(student.getEndate());

        // 确保字段不可编辑
        setFieldsEditable(false);
    }
}