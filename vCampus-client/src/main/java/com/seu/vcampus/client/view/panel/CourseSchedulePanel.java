package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.common.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CourseSchedulePanel extends JPanel {
    private JTable scheduleTable;
    private JComboBox<Integer> weekSelector;
    private User currentUser;
    public CourseSchedulePanel(User user) {
        this.currentUser=user;
        setLayout(new BorderLayout());

        // 顶部周数选择栏
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("选择周数:"));
        weekSelector = new JComboBox<>();
        for (int i = 1; i <= 20; i++) {
            weekSelector.addItem(i);
        }
        weekSelector.setSelectedItem(1); // 默认显示第1周
        weekSelector.addActionListener(this::loadScheduleData);
        topPanel.add(weekSelector);

        add(topPanel, BorderLayout.NORTH);

        // 课表表格
        String[] columns = {"时间/星期", "周一", "周二", "周三", "周四", "周五"};
        scheduleTable = new JTable(new Object[5][6], columns);
        scheduleTable.setRowHeight(60);
        add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

    }

    private void loadScheduleData(ActionEvent e) {
        int selectedWeek = (int) weekSelector.getSelectedItem();
        // 根据周数加载课表数据
        updateScheduleTable(selectedWeek);
    }

    private void updateScheduleTable(int week) {
        // 模拟数据 - 实际应从服务端获取
        Object[][] data = new Object[5][6];
        data[0] = new Object[]{"8:00-9:40", "", "", "", "", ""};
        data[1] = new Object[]{"10:00-11:40", "", "高等数学", "", "", ""};
        data[2] = new Object[]{"13:30-15:10", "", "", "大学物理", "", ""};
        data[3] = new Object[]{"15:30-17:10", "", "", "", "大学英语", ""};
        data[4] = new Object[]{"18:30-20:10", "", "", "", "", ""};

        // 第1周特殊示例数据
        if (week == 1) {
            data[0][1] = "计算机科学导论\n王教授\n三教201";
            data[1][2] = "高等数学\n李教授\n一教101";
        }

        // 更新表格模型
        scheduleTable.setModel(new DefaultTableModel(data,
                new String[]{"时间/星期", "周一", "周二", "周三", "周四", "周五"}));
    }
}