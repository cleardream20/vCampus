package com.seu.vcampus.client.view.panel.StudentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.seu.vcampus.client.controller.StudentController;
import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.client.view.frame.MainFrame;
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

    public STPanel() {
//        User user = MainFrame.getInstance().getCurrentUser();
        User user = new User();
        String userId = user.getCid();
        StudentController controller = new StudentController();
        this.currentStudent = controller.getStudent(userId);
        initializeUI();
        setFieldsEditable(false);
        submitButton.setEnabled(false);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 标题
        JLabel titleLabel = new JLabel("学生个人信息", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // 表单面板
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 姓名
        formPanel.add(new JLabel("姓名:"));
        nameField = new JTextField(currentStudent.getName());
        formPanel.add(nameField);

        // 邮箱
        formPanel.add(new JLabel("邮箱:"));
        emailField = new JTextField(currentStudent.getEmail());
        formPanel.add(emailField);

        // 电话
        formPanel.add(new JLabel("电话:"));
        phoneField = new JTextField(currentStudent.getPhone());
        formPanel.add(phoneField);

        // 性别
        formPanel.add(new JLabel("性别:"));
        sexField = new JTextField(currentStudent.getSex());
        formPanel.add(sexField);

        // 出生日期
        formPanel.add(new JLabel("出生日期:"));
        birthdayField = new JTextField(currentStudent.getBirthday());
        formPanel.add(birthdayField);

        // 地址
        formPanel.add(new JLabel("地址:"));
        addressField = new JTextField(currentStudent.getAddress());
        formPanel.add(addressField);

        // 身份证号
        formPanel.add(new JLabel("身份证号:"));
        nidField = new JTextField(currentStudent.getNid());
        formPanel.add(nidField);

        // 入学日期
        formPanel.add(new JLabel("入学日期:"));
        endateField = new JTextField(currentStudent.getEndate());
        formPanel.add(endateField);

        add(formPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        modifyButton = new JButton("修改");
        submitButton = new JButton("提交");
        refreshButton = new JButton("刷新");

        buttonPanel.add(modifyButton);
        buttonPanel.add(submitButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 添加事件监听
        setupEventListeners();
    }

    private void setupEventListeners() {
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setFieldsEditable(true);
                modifyButton.setEnabled(false);
                submitButton.setEnabled(true);
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                // boolean success = submitChangesToServer(currentStudent);

                // 假设提交成功
                JOptionPane.showMessageDialog(STPanel.this,
                        "修改申请已提交，等待审核", "提示", JOptionPane.INFORMATION_MESSAGE);

                setFieldsEditable(false);
                modifyButton.setEnabled(true);
                submitButton.setEnabled(false);
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 这里调用服务器接口获取最新数据
                // Student updatedStudent = fetchLatestStudentData();

                // 假设从服务器获取了更新后的数据
                // refreshPanel(updatedStudent);

                JOptionPane.showMessageDialog(STPanel.this,
                        "页面已刷新", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
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
        Color bgColor = editable ? new Color(240, 240, 240) : Color.WHITE;
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
        return "Student";
    }

    @Override
    public void refreshPanel(User user) {
        String userId = user.getCid();
        StudentController controller = new StudentController();
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