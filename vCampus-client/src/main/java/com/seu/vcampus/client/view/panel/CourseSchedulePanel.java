package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.controller.CourseController;
import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.CourseSchedule;
import com.seu.vcampus.common.model.User;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseSchedulePanel extends JPanel {
    private JTable scheduleTable;
    private JComboBox<Integer> weekSelector;
    private JComboBox<String> semesterSelector;
    private User currentUser;
    private CourseController courseController;

    // 时间段定义
    private static final String[] TIME_SLOTS = {
            "8:00-8:45",   // 第1节
            "8:50-9:35",   // 第2节
            "9:50-10:35",  // 第3节
            "10:40-11:25", // 第4节
            "11:30-12:15", // 第5节
            "14:00-14:45", // 第6节
            "14:50-15:35", // 第7节
            "15:50-16:35", // 第8节
            "16:40-17:25", // 第9节
            "17:30-18:15", // 第10节
            "19:00-19:45", // 第11节
            "19:50-20:35", // 第12节
            "20:40-21:25"  // 第13节
    };

    public CourseSchedulePanel(User user) {
        this.currentUser = user;
        this.courseController = new CourseController();
        setLayout(new BorderLayout());
        initUI();
        loadSemesters();
    }

    private void initUI() {
        // 顶部面板：学期选择和周数选择
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(new Color(240, 245, 255)); // 浅蓝色背景

        // 学期选择
        topPanel.add(new JLabel("选择学期:"));
        semesterSelector = new JComboBox<>();
        semesterSelector.setPreferredSize(new Dimension(150, 30));
        semesterSelector.addActionListener(this::loadScheduleData);
        topPanel.add(semesterSelector);

        // 周数选择
        topPanel.add(new JLabel("选择周数:"));
        weekSelector = new JComboBox<>();
        weekSelector.setPreferredSize(new Dimension(80, 30));
        for (int i = 1; i <= 20; i++) {
            weekSelector.addItem(i);
        }
        weekSelector.setSelectedItem(1);
        weekSelector.addActionListener(this::loadScheduleData);
        topPanel.add(weekSelector);

        // 课表表格
        String[] columns = {"时间/星期", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        DefaultTableModel model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scheduleTable = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                int height = Math.max(50, comp.getPreferredSize().height + 10);
                if (getRowHeight(row) != height) {
                    setRowHeight(row, height);
                }
                return comp;
            }
        };

        // 设置表格属性
        scheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scheduleTable.setFillsViewportHeight(true);
        scheduleTable.setRowHeight(50);
        scheduleTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        scheduleTable.setGridColor(new Color(200, 200, 200));
        scheduleTable.setShowGrid(true);

        // 初始化时间列
        model.setRowCount(TIME_SLOTS.length);
        for (int i = 0; i < TIME_SLOTS.length; i++) {
            model.setValueAt(TIME_SLOTS[i], i, 0);
        }

        // 表头样式 - 关键修改点：去除右侧多余蓝条
        JTableHeader header = scheduleTable.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 16));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // 单元格渲染器
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setVerticalAlignment(JLabel.CENTER);
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                return this;
            }
        };

        // 设置列宽
        TableColumnModel columnModel = scheduleTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120);
        for (int i = 1; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(150);
            columnModel.getColumn(i).setCellRenderer(centerRenderer);
        }

        // 关键修改点：使用自定义的ScrollPane来去除右侧蓝条
        JScrollPane scrollPane = new JScrollPane(scheduleTable) {
            @Override
            public void setBounds(int x, int y, int width, int height) {
                // 确保右侧不会超出边界
                int adjustedWidth = Math.min(width, getParent().getWidth() - x);
                super.setBounds(x, y, adjustedWidth, height);
            }
        };
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // 使用BorderLayout并添加组件
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 添加组件监听器
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustTableColumns();
            }
        });
    }

    private void adjustTableColumns() {
        if (scheduleTable == null) return;

        // 计算可用宽度（减去边距）
        int availableWidth = getWidth() - 20; // 减少边距

        TableColumnModel columnModel = scheduleTable.getColumnModel();
        int totalColumns = columnModel.getColumnCount();
        int baseWidth = availableWidth / totalColumns;

        // 设置时间列宽度（稍窄）
        columnModel.getColumn(0).setPreferredWidth((int)(baseWidth * 0.8));

        // 设置课程列宽度
        for (int i = 1; i < totalColumns; i++) {
            columnModel.getColumn(i).setPreferredWidth(baseWidth);
        }

        // 关键修改点：确保周日列不会超出边界
        int totalWidth = (int)(baseWidth * 0.8) + (baseWidth * (totalColumns - 1));
        if (totalWidth > availableWidth) {
            columnModel.getColumn(totalColumns - 1).setPreferredWidth(
                    baseWidth - (totalWidth - availableWidth)
            );
        }

        scheduleTable.revalidate();
    }

    private void loadSemesters() {
        List<String> semesters = generateSemesters();
        Collections.sort(semesters, Collections.reverseOrder());

        semesterSelector.removeAllItems();
        for (String semester : semesters) {
            semesterSelector.addItem(semester);
        }

        // 设置默认学期为2025-2026-1（根据图片中的选择）
        semesterSelector.setSelectedItem("2025-2026-1");
        loadScheduleData(null);
    }

    private String getCurrentSemester() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        Month month = now.getMonth();

        int semesterNum;
        if (month.getValue() >= 9) {
            semesterNum = 1;
        } else if (month.getValue() >= 5) {
            semesterNum = 3;
            year--;
        } else {
            semesterNum = 2;
            year--;
        }

        return String.format("%d-%d-%d", year, year + 1, semesterNum);
    }

    private List<String> generateSemesters() {
        List<String> semesters = new ArrayList<>();

        // 2023-2024学年
        semesters.add("2023-2024-1");
        semesters.add("2023-2024-2");
        semesters.add("2023-2024-3");

        // 2024-2025学年
        semesters.add("2024-2025-1");
        semesters.add("2024-2025-2");
        semesters.add("2024-2025-3");

        // 2025-2026学年
        semesters.add("2025-2026-1");

        return semesters;
    }

    private void loadScheduleData(ActionEvent e) {
        String semester = (String) semesterSelector.getSelectedItem();
        int week = (int) weekSelector.getSelectedItem();

        if (semester == null || semester.isEmpty()) {
            return;
        }

        CourseSchedule courses = courseController.getCoursesSchedual(
                currentUser.getId(), currentUser, semester
        );

        updateScheduleTable(courses, week);
    }

    private void updateScheduleTable(CourseSchedule courseSchedule, int currentWeek) {
        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();

        // 清空课表内容（保留时间列）
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 1; col < model.getColumnCount(); col++) {
                model.setValueAt("", row, col);
            }
        }

        if (courseSchedule == null || courseSchedule.getCourses() == null ||
                courseSchedule.getCourses().isEmpty()) {
            return;
        }

        // 填充课程数据
        for (Course course : courseSchedule.getCourses()) {
            // 检查课程是否在当前周
            if (course.getStartWeek() > currentWeek || course.getEndWeek() < currentWeek) {
                continue;
            }

            // 解析时间安排字符串（格式："周一 3-4节"）
            String[] scheduleParts = course.getSchedule().split(" ");
            if (scheduleParts.length < 2) continue;

            String weekday = scheduleParts[0];
            String timeSlot = scheduleParts[1].replace("节", "");

            // 获取星期对应的列索引
            int column = convertWeekdayToColumn(weekday);
            if (column == -1) continue;

            // 获取时间段对应的行索引
            int row = convertTimeSlotToRow(timeSlot);
            if (row == -1) continue;

            // 构建课程信息字符串（只显示课程名称和地点）
            String courseInfo = String.format("<html><div style='text-align:center;padding:3px;'>" +
                            "<b>%s</b><br>" +
                            "<span style='color:#666;'>%s</span></div></html>",
                    course.getCourseName(),
                    course.getLocation()
            );

            model.setValueAt(courseInfo, row, column);
        }

        // 调整列宽
        adjustTableColumns();
    }

    private int convertWeekdayToColumn(String weekday) {
        return switch (weekday) {
            case "周一" -> 1;
            case "周二" -> 2;
            case "周三" -> 3;
            case "周四" -> 4;
            case "周五" -> 5;
            case "周六" -> 6;
            case "周日" -> 7;
            default -> -1;
        };
    }

    private int convertTimeSlotToRow(String timeSlot) {
        String[] parts = timeSlot.split("-");
        if (parts.length != 2) return -1;

        try {
            int start = Integer.parseInt(parts[0]);
            return start - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}