package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.controller.CourseController;
import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CourseManagementPanel extends JPanel {
    private JTable courseTable;
    private CourseController courseController;
    private User currentUser;
    public CourseManagementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        initUI();
        loadCourseData();
    }

    private void initUI() {
        // 操作按钮组
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addButton = new JButton("新增课程");
        JButton editButton = new JButton("修改课程");
        JButton deleteButton = new JButton("删除课程");

        addButton.addActionListener(this::showAddCourseDialog);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.NORTH);

        // 初始化空表格
        String[] columns = {"课程ID", "课程名称", "学分", "授课教师","教师id", "地点","时间安排", "容量", "已选人数", "开始周", "结束周"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        courseTable = new JTable(model);

        // 设置表格样式
        courseTable.setRowHeight(30);
        courseTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 表头样式
        JTableHeader header = courseTable.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);

        add(new JScrollPane(courseTable), BorderLayout.CENTER);
    }

    private void showAddCourseDialog(ActionEvent e) {
        // 创建对话框 - 增大尺寸
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "添加新课程", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500); // 增大对话框尺寸
        dialog.setLocationRelativeTo(this);

        // 创建表单面板 - 使用GridBagLayout实现更精确的布局控制
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // 增加左右边距
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // 增加组件间距
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 创建表单字段
        JTextField courseIdField = new JTextField(10);
        JTextField courseNameField = new JTextField(10);
        JTextField teacherNameField = new JTextField(10);
        JTextField teacherIdField = new JTextField(10);
        JTextField departmentField = new JTextField(10);
        JSpinner creditSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JTextField timeField = new JTextField(10);
        JTextField locationField = new JTextField(10);
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 300, 1));
        JSpinner startWeekSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        JSpinner endWeekSpinner = new JSpinner(new SpinnerNumberModel(16, 1, 20, 1));

        // 设置标签右对齐
        JLabel courseIdLabel = new JLabel("课程ID:");
        courseIdLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel courseNameLabel = new JLabel("课程名称:");
        courseNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel teacherNameLabel = new JLabel("授课教师:");
        teacherNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel teacherIdLabel = new JLabel("教师ID:");
        teacherIdLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel departmentLabel = new JLabel("开课院系:");
        departmentLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel creditLabel = new JLabel("学分:");
        creditLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel timeLabel = new JLabel("时间安排:");
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel locationLabel = new JLabel("上课地点:");
        locationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel capacityLabel = new JLabel("课程容量:");
        capacityLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel startWeekLabel = new JLabel("开始周:");
        startWeekLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel endWeekLabel = new JLabel("结束周:");
        endWeekLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // 添加表单标签和字段 - 使用GridBagLayout布局
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2; // 标签列占30%宽度
        formPanel.add(courseIdLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7; // 输入框列占70%宽度
        formPanel.add(courseIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(courseNameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(courseNameField, gbc);

        // 继续添加其他字段...
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(teacherNameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(teacherNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        formPanel.add(teacherIdLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(teacherIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        formPanel.add(departmentLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(departmentField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        formPanel.add(creditLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(creditSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.3;
        formPanel.add(timeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(timeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.3;
        formPanel.add(locationLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(locationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0.3;
        formPanel.add(capacityLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(capacitySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 0.3;
        formPanel.add(startWeekLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(startWeekSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.weightx = 0.3;
        formPanel.add(endWeekLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(endWeekSpinner, gbc);

        // 创建按钮面板 - 居中对齐
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton submitButton = new JButton("提交");
        JButton cancelButton = new JButton("取消");

        // 提交按钮事件
        submitButton.addActionListener(ev -> {
            // 验证必填字段
            if (courseIdField.getText().isEmpty() ||
                    courseNameField.getText().isEmpty() ||
                    teacherNameField.getText().isEmpty() ||
                    teacherIdField.getText().isEmpty() ||
                    timeField.getText().isEmpty() ||
                    locationField.getText().isEmpty()) {

                JOptionPane.showMessageDialog(dialog, "请填写所有字段", "输入错误", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 创建新课程对象
            Course newCourse = new Course();
            newCourse.setCourseId(courseIdField.getText());
            newCourse.setCourseName(courseNameField.getText());
            newCourse.setTeacherName(teacherNameField.getText());
            newCourse.setTeacherId(teacherIdField.getText());
            newCourse.setDepartment(departmentField.getText());
            newCourse.setCredit((Integer) creditSpinner.getValue());
            newCourse.setSchedule(timeField.getText());
            newCourse.setLocation(locationField.getText());
            newCourse.setCapacity((Integer) capacitySpinner.getValue());
            newCourse.setSelectedNum(0); // 新课程已选人数默认为0
            newCourse.setStartWeek((Integer) startWeekSpinner.getValue());
            newCourse.setEndWeek((Integer) endWeekSpinner.getValue());

            // 通过控制器添加课程
            if (courseController.addCourse(newCourse,currentUser)) {
                JOptionPane.showMessageDialog(dialog, "课程添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadCourseData(); // 刷新表格数据
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "课程添加失败，请检查数据", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 取消按钮事件
        cancelButton.addActionListener(ev -> dialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        // 添加组件到对话框
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // 显示对话框
        dialog.setVisible(true);
    }

    private void loadCourseData() {
        // 初始化控制器
        courseController = new CourseController();

        // 从服务器获取课程数据
        List<Course> courses = courseController.getCourseList();

        if (courses != null && !courses.isEmpty()) {
            DefaultTableModel model = (DefaultTableModel) courseTable.getModel();
            model.setRowCount(0); // 清空现有数据

            for (Course course : courses) {
                model.addRow(new Object[]{
                        course.getCourseId(),
                        course.getCourseName(),
                        course.getCredit(),
                        course.getTeacherName(),
                        course.getTeacherId(),
                        course.getDepartment(),
                        course.getSchedule(),
                        course.getCapacity(),
                        course.getSelectedNum(),
                        course.getStartWeek(),
                        course.getEndWeek()
                });
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "加载课程数据失败",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}