package com.seu.vcampus.client.view.panel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class CourseQueryPanel extends JPanel {
    private JTable courseTable;
    private JTextField searchField;

    public CourseQueryPanel() {
        setLayout(new BorderLayout());

        // ============== 放大后的搜索栏 ==============
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 放大搜索标签
        JLabel searchLabel = new JLabel("课程搜索：");
        searchLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));

        // 放大输入框（宽度300px，高度35px）
        searchField = new JTextField();
        searchField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(250, 30));

        // 放大按钮（宽度100px，高度35px）
        JButton searchButton = new JButton("搜索");
        searchButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        searchButton.setPreferredSize(new Dimension(100, 35));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);
        // ========================================

        // 表格部分（保持不变）
        String[] columns = {"课程ID", "课程名称", "学分", "授课教师", "时间安排", "容量", "状态"};
        Object[][] data = {
                {"CS101", "计算机科学导论", 3, "T001", "周一8:00-9:40", "1/100", "可选"},
                {"MA201", "高等数学", 4, "T002", "周二10:00-11:40", "68/150", "可选"},
                {"PH301", "大学物理", 4, "T003", "周三13:30-15:10", "51/120", "可选"},
                {"EN401", "大学英语", 2, "T004", "周四15:30-17:10", "40/80", "可选"},
                {"SE501", "软件工程", 3, "T005", "周五8:00-9:40", "6/60", "可选"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 所有单元格不可编辑
            }
        };

        courseTable = new JTable(model);

        // 表格样式设置
        courseTable.setRowHeight(30);
        courseTable.setIntercellSpacing(new Dimension(0, 0));

        add(new JScrollPane(courseTable), BorderLayout.CENTER);
    }
}