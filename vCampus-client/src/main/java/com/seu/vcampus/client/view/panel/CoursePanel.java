package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.controller.CourseController;
import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.User;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CoursePanel extends JPanel {
    private final CourseController courseController;
    private final User currentUser;

    private JTable availableCoursesTable;
    private JTable selectedCoursesTable;
    private JButton selectButton;
    private JButton dropButton;
    private JButton refreshButton;

    public CoursePanel(CourseController courseController, User user) {
        this.courseController = courseController;
        this.currentUser = user;
        initComponents();
        loadCourseData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 创建分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // 可用课程面板
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("可选课程"));
        availableCoursesTable = new JTable();
        availablePanel.add(new JScrollPane(availableCoursesTable), BorderLayout.CENTER);

        // 已选课程面板
        JPanel selectedPanel = new JPanel(new BorderLayout());
        selectedPanel.setBorder(BorderFactory.createTitledBorder("已选课程"));
        selectedCoursesTable = new JTable();
        selectedPanel.add(new JScrollPane(selectedCoursesTable), BorderLayout.CENTER);

        splitPane.setTopComponent(availablePanel);
        splitPane.setBottomComponent(selectedPanel);

        add(splitPane, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        selectButton = new JButton("选课");
        dropButton = new JButton("退课");
        refreshButton = new JButton("刷新");

        buttonPanel.add(selectButton);
        buttonPanel.add(dropButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 添加事件监听
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectCourse();
            }
        });

        dropButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dropCourse();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCourseData();
            }
        });
    }

    private void loadCourseData() {
        // 模拟课程数据
        List<Course> availableCourses = new ArrayList<>();
        availableCourses.add(new Course("C001", "高等数学", "T001", "数学系", 4, "周一 1-2节", "教一101", 100, 80, ""));
        availableCourses.add(new Course("C002", "大学英语", "T002", "外语系", 3, "周二 3-4节", "教二201", 80, 75, ""));
        availableCourses.add(new Course("C003", "程序设计", "T003", "计算机系", 4, "周三 5-6节", "计算中心301", 60, 55, ""));

        List<Course> selectedCourses = new ArrayList<>();
        selectedCourses.add(new Course("C004", "线性代数", "T004", "数学系", 3, "周四 1-2节", "教一102", 100, 85, ""));

        // 更新表格
        updateTable(availableCoursesTable, availableCourses);
        updateTable(selectedCoursesTable, selectedCourses);
    }

    private void updateTable(JTable table, List<Course> courses) {
        // 创建表格模型
        CourseTableModel model = new CourseTableModel(courses);
        table.setModel(model);
    }

    private void selectCourse() {
        int selectedRow = availableCoursesTable.getSelectedRow();
        if (selectedRow >= 0) {
            // 实际应该调用courseController.selectCourse()方法
            JOptionPane.showMessageDialog(this, "选课成功");
            loadCourseData();
        } else {
            JOptionPane.showMessageDialog(this, "请选择要选的课程", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void dropCourse() {
        int selectedRow = selectedCoursesTable.getSelectedRow();
        if (selectedRow >= 0) {
            // 实际应该调用courseController.dropCourse()方法
            JOptionPane.showMessageDialog(this, "退课成功");
            loadCourseData();
        } else {
            JOptionPane.showMessageDialog(this, "请选择要退的课程", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    // 自定义表格模型
    private class CourseTableModel extends AbstractTableModel {
        private List<Course> courses;
        private String[] columnNames = {"课程ID", "课程名称", "教师ID", "学分", "时间", "地点", "容量", "已选"};

        public CourseTableModel(List<Course> courses) {
            this.courses = courses;
        }

        @Override
        public int getRowCount() {
            return courses.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Course course = courses.get(rowIndex);
            switch (columnIndex) {
                case 0: return course.getCourseId();
                case 1: return course.getCourseName();
                case 2: return course.getTeacherId(); // 使用getTeacherId()而不是getTeacherName()
                case 3: return course.getCredit();
                case 4: return course.getTime();
                case 5: return course.getLocation();
                case 6: return course.getCapacity();
                case 7: return course.getSelectedNum();
                default: return null;
            }
        }
    }
}