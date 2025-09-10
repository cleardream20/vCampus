package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.common.model.User;

import javax.swing.*;
import java.awt.*;

public class SelectionReportPanel extends JPanel {
    private User currentUser;
    public SelectionReportPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());

        // 课程选择
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JComboBox<String> courseSelector = new JComboBox<>(new String[]{
                "CS101 - 计算机科学导论",
                "MA201 - 高等数学",
                "PH301 - 大学物理"
        });
        topPanel.add(new JLabel("选择课程:"));
        topPanel.add(courseSelector);
        add(topPanel, BorderLayout.NORTH);

        // 选课名单表格（样式与图片一致）
        String[] columns = {"学号", "姓名", "院系", "选课时间"};
        JTable studentTable = new JTable(new Object[][]{
                {"20210001", "张三", "计算机学院", "2023-09-01"},
                {"20210002", "李四", "数学学院", "2023-09-02"}
        }, columns);

        studentTable.setRowHeight(30);
        studentTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // 导出按钮
        JButton exportButton = new JButton("导出名单");
        add(exportButton, BorderLayout.SOUTH);
    }
}