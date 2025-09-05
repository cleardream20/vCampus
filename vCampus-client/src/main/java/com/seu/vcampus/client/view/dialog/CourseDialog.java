// CourseDialog.java
package com.seu.vcampus.client.view.dialog;

import com.seu.vcampus.common.model.Course;
import javax.swing.*;
import java.awt.*;

public class CourseDialog extends JDialog {
    private Course course;
    private boolean confirmed = false;

    private JTextField courseIdField;
    private JTextField courseNameField;
    private JTextField teacherIdField;
    private JTextField departmentField;
    private JSpinner creditSpinner;
    private JTextField timeField;
    private JTextField locationField;
    private JSpinner capacitySpinner;
    private JTextArea prerequisitesArea;

    public CourseDialog(JFrame parent) {
        super(parent, "添加课程", true);
        this.course = new Course();
        initComponents();
    }

    public CourseDialog(JFrame parent, Course course) {
        super(parent, "编辑课程", true);
        this.course = course;
        initComponents();
        fillFields();
    }

    private void initComponents() {
        setLayout(new GridLayout(9, 2, 5, 5));
        setSize(400, 500);

        add(new JLabel("课程ID:"));
        courseIdField = new JTextField();
        add(courseIdField);

        add(new JLabel("课程名称:"));
        courseNameField = new JTextField();
        add(courseNameField);

        add(new JLabel("教师ID:"));
        teacherIdField = new JTextField();
        add(teacherIdField);

        add(new JLabel("开课院系:"));
        departmentField = new JTextField();
        add(departmentField);

        add(new JLabel("学分:"));
        creditSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
        add(creditSpinner);

        add(new JLabel("上课时间:"));
        timeField = new JTextField();
        add(timeField);

        add(new JLabel("上课地点:"));
        locationField = new JTextField();
        add(locationField);

        add(new JLabel("课程容量:"));
        capacitySpinner = new JSpinner(new SpinnerNumberModel(50, 1, 300, 1));
        add(capacitySpinner);

        add(new JLabel("先修要求:"));
        prerequisitesArea = new JTextArea(3, 20);
        add(new JScrollPane(prerequisitesArea));

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        add(new JLabel());
        add(buttonPanel);

        okButton.addActionListener(e -> {
            confirmed = true;
            updateCourse();
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());
    }

    private void fillFields() {
        courseIdField.setText(course.getCourseId());
        courseNameField.setText(course.getCourseName());
        teacherIdField.setText(course.getTeacherId());
        departmentField.setText(course.getDepartment());
        creditSpinner.setValue(course.getCredit());
        timeField.setText(course.getTime());
        locationField.setText(course.getLocation());
        capacitySpinner.setValue(course.getCapacity());
        prerequisitesArea.setText(course.getPrerequisites());
    }

    private void updateCourse() {
        course.setCourseId(courseIdField.getText());
        course.setCourseName(courseNameField.getText());
        course.setTeacherId(teacherIdField.getText());
        course.setDepartment(departmentField.getText());
        course.setCredit((Integer) creditSpinner.getValue());
        course.setTime(timeField.getText());
        course.setLocation(locationField.getText());
        course.setCapacity((Integer) capacitySpinner.getValue());
        course.setPrerequisites(prerequisitesArea.getText());
    }

    public boolean showDialog() {
        setVisible(true);
        return confirmed;
    }

    public Course getCourse() {
        return course;
    }
}