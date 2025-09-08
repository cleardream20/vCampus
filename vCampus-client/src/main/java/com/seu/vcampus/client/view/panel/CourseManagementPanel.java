package com.seu.vcampus.client.view.panel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class CourseManagementPanel extends JPanel {
    public CourseManagementPanel() {
        setLayout(new BorderLayout());

        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addButton = new JButton("新增课程");
        JButton editButton = new JButton("修改课程");
        JButton deleteButton = new JButton("删除课程");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.NORTH);

        // 课程表格（样式与图片一致）
        String[] columns = {"课程ID", "课程名称", "学分", "授课教师", "时间安排", "容量", "状态"};
        Object[][] data = {
                {"CS101", "计算机科学导论", 3, "T001", "周一8:00-9:40", "100", "可选"},
                {"MA201", "高等数学", 4, "T002", "周二10:00-11:40", "150", "可选"},
                {"PH301", "大学物理", 4, "T003", "周三13:30-15:10", "120", "可选"},
                {"EN401", "大学英语", 2, "T004", "周四15:30-17:10", "80", "可选"},
                {"SE501", "软件工程", 3, "T005", "周五8:00-9:40", "60", "可选"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable courseTable = new JTable(model);
        courseTable.setRowHeight(30);
        courseTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 表头样式（与图片中的蓝色风格一致）
        JTableHeader header = courseTable.getTableHeader();
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));

        add(new JScrollPane(courseTable), BorderLayout.CENTER);
    }
}