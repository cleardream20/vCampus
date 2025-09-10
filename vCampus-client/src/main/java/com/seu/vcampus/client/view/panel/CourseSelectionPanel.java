package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.common.model.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class CourseSelectionPanel extends JPanel {
    private JTable availableCoursesTable;
    private JButton selectButton;
    private User currentUser;
    public CourseSelectionPanel(User user) {
        this.currentUser=user;
        setLayout(new BorderLayout());

        // ============== 表格数据 ==============
        String[] columns = {"课程ID", "课程名称", "学分", "授课教师", "时间安排", "容量", "状态"};
        Object[][] data = {
                {"CS101", "计算机科学导论", 3, "王教授", "周一8:00-9:40", "1/100", "可选"},
                {"MA201", "高等数学", 4, "李教授", "周二10:00-11:40", "68/150", "可选"},
                {"PH301", "大学物理", 4, "张教授", "周三13:30-15:10", "51/120", "可选"},
                {"EN401", "大学英语", 2, "刘教授", "周四15:30-17:10", "40/80", "可选"},
                {"SE501", "软件工程", 3, "陈教授", "周五8:00-9:40", "6/60", "可选"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        availableCoursesTable = new JTable(model);

        // ============== 表格样式 ==============
        availableCoursesTable.setRowHeight(30);
        availableCoursesTable.setSelectionBackground(new Color(173, 216, 230));

        // 表头样式
        JTableHeader header = availableCoursesTable.getTableHeader();
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));

        // ============== 底部操作栏 ==============
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // 状态标签
        JLabel statusLabel = new JLabel("数据加载完成 学生: 张三(20210001) 系统就绪");
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        selectButton = new JButton("选课");
        selectButton.setPreferredSize(new Dimension(80, 30));

        JButton dropButton = new JButton("退课");
        dropButton.setPreferredSize(new Dimension(80, 30));

        buttonPanel.add(selectButton);
        buttonPanel.add(dropButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // ============== 组装界面 ==============
        add(new JScrollPane(availableCoursesTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}