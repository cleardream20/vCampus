// CourseManagementPanel.java
package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.view.dialog.CourseDialog;
import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.client.controller.AdminController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CourseManagementPanel extends JPanel {
    private AdminController adminController;
    private JTable courseTable;
    private JButton addButton, editButton, deleteButton;

    public CourseManagementPanel(AdminController adminController) {
        this.adminController = adminController;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 课程表格
        courseTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(courseTable);
        add(scrollPane, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("添加课程");
        editButton = new JButton("编辑课程");
        deleteButton = new JButton("删除课程");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 添加事件监听
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCourse();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editCourse();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCourse();
            }
        });
    }

    private void addCourse() {
        // 创建添加课程对话框
        CourseDialog dialog = new CourseDialog((JFrame)SwingUtilities.getWindowAncestor(this));
        if (dialog.showDialog()) {
            Course course = dialog.getCourse();
            if (adminController.addCourse(course)) {
                JOptionPane.showMessageDialog(this, "课程添加成功");
                // 刷新表格
            } else {
                JOptionPane.showMessageDialog(this, "课程添加失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow >= 0) {
            // 获取选中的课程
            Course course = getCourseFromTable(selectedRow);

            // 创建编辑对话框
            CourseDialog dialog = new CourseDialog((JFrame)SwingUtilities.getWindowAncestor(this), course);
            if (dialog.showDialog()) {
                Course updatedCourse = dialog.getCourse();
                if (adminController.updateCourse(updatedCourse)) {
                    JOptionPane.showMessageDialog(this, "课程更新成功");
                    // 刷新表格
                } else {
                    JOptionPane.showMessageDialog(this, "课程更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "请选择要编辑的课程", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteCourse() {
        int selectedRow = courseTable.getSelectedRow();
        if (selectedRow >= 0) {
            Course course = getCourseFromTable(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "确定要删除课程: " + course.getCourseName() + "?",
                    "确认删除", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (adminController.deleteCourse(course.getCourseId())) {
                    JOptionPane.showMessageDialog(this, "课程删除成功");
                    // 刷新表格
                } else {
                    JOptionPane.showMessageDialog(this, "课程删除失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "请选择要删除的课程", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    private Course getCourseFromTable(int row) {
        // 从表格中获取课程对象
        return new Course();
    }
}