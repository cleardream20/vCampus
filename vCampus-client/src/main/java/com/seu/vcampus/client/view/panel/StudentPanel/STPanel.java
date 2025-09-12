package com.seu.vcampus.client.view.panel.StudentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.seu.vcampus.client.controller.Student.STController;
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
    private JButton modifyButton;
    private JButton submitButton;
    private JButton refreshButton;
    private JButton backButton; // 添加返回按钮

    public STPanel() {
//        User user = MainFrame.getInstance().getCurrentUser();
        User user = new User();
        String userId = user.getCid();
        STController controller = new STController();
        this.currentStudent = controller.getStudent(userId);
        initializeUI();
        setFieldsEditable(false);
        submitButton.setEnabled(false);
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

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        modifyButton = new JButton("修改");
        submitButton = new JButton("提交");
        refreshButton = new JButton("刷新");

        modifyButton.setFocusPainted(false);
        submitButton.setFocusPainted(false);
        refreshButton.setFocusPainted(false);

        buttonPanel.add(modifyButton);
        buttonPanel.add(submitButton);
        buttonPanel.add(refreshButton);

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 将内容面板添加到主面板
        add(contentPanel, BorderLayout.CENTER);

        // 添加事件监听
        setupEventListeners();
    }

    private void setupEventListeners() {
        modifyButton.addActionListener(e -> {
            setFieldsEditable(true);
            modifyButton.setEnabled(false);
            submitButton.setEnabled(true);
        });

        submitButton.addActionListener(e -> {
            // 更新学生对象
            currentStudent.setName(nameField.getText());
            currentStudent.setEmail(emailField.getText());
            currentStudent.setPhone(phoneField.getText());
            currentStudent.setSex(sexField.getText());
            currentStudent.setBirthday(birthdayField.getText());
            currentStudent.setAddress(addressField.getText());
            currentStudent.setNid(nidField.getText());
            currentStudent.setEndate(endateField.getText());

            // 这里调用服务器接口提交修改
            boolean success = true;

            // 假设提交成功
            JOptionPane.showMessageDialog(STPanel.this,
                    "修改申请已提交，等待审核", "提示", JOptionPane.INFORMATION_MESSAGE);

            setFieldsEditable(false);
            modifyButton.setEnabled(true);
            submitButton.setEnabled(false);
        });

        refreshButton.addActionListener(e -> {
            // 这里调用服务器接口获取最新数据
            // Student updatedStudent = fetchLatestStudentData();
            Student updateStudent = new Student();
            // 假设从服务器获取了更新后的数据
             refreshPanel(updateStudent);

            JOptionPane.showMessageDialog(STPanel.this,
                    "页面已刷新", "提示", JOptionPane.INFORMATION_MESSAGE);
        });

        // 添加返回按钮的事件监听
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
        STController controller = new STController();
        Student student = controller.getStudent(userId);
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

        // 重置编辑状态
        setFieldsEditable(false);
        modifyButton.setEnabled(true);
        submitButton.setEnabled(false);
    }
}