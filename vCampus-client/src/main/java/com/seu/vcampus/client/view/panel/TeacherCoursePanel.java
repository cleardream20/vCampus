// TeacherCoursePanel.java
package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.client.controller.CourseController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TeacherCoursePanel extends JPanel {
    private CourseController courseController;
    private User currentUser;

    private JTable teachingCoursesTable;
    private JTable studentSelectionTable;

    public TeacherCoursePanel(CourseController courseController, User user) {
        this.courseController = courseController;
        this.currentUser = user;
        initComponents();
        loadTeachingCourses();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 创建分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // 授课课程面板
        JPanel teachingPanel = new JPanel(new BorderLayout());
        teachingPanel.setBorder(BorderFactory.createTitledBorder("授课课程"));
        teachingCoursesTable = new JTable();
        teachingPanel.add(new JScrollPane(teachingCoursesTable), BorderLayout.CENTER);

        // 学生选课情况面板
        JPanel studentPanel = new JPanel(new BorderLayout());
        studentPanel.setBorder(BorderFactory.createTitledBorder("学生选课情况"));
        studentSelectionTable = new JTable();
        studentPanel.add(new JScrollPane(studentSelectionTable), BorderLayout.CENTER);

        splitPane.setTopComponent(teachingPanel);
        splitPane.setBottomComponent(studentPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private void loadTeachingCourses() {
        // 加载教师授课的课程
        List<Course> teachingCourses = courseController.getTeachingCourses(currentUser.getId());
        updateTable(teachingCoursesTable, teachingCourses);
    }

    private void updateTable(JTable table, List<Course> courses) {
        // 更新表格数据
    }
}