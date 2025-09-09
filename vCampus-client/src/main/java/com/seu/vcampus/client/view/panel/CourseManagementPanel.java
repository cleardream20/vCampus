package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.controller.CourseController;
import com.seu.vcampus.common.model.Course;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class CourseManagementPanel extends JPanel {
    private JTable courseTable;
    private CourseController courseController;

    public CourseManagementPanel() {
        setLayout(new BorderLayout());
        initUI();
        loadCourseData();
    }

    private void initUI() {
        // 操作按钮组
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addButton = new JButton("新增课程");
        JButton editButton = new JButton("修改课程");
        JButton deleteButton = new JButton("删除课程");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.NORTH);

        // 初始化空表格
        String[] columns = {"课程ID", "课程名称", "学分", "授课教师","教师id", "时间安排", "容量", "已选人数", "开始周", "结束周"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        courseTable = new JTable(model);

        // 设置表格样式
        courseTable.setRowHeight(30);
        courseTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 表头样式
        JTableHeader header = courseTable.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);

        add(new JScrollPane(courseTable), BorderLayout.CENTER);
    }

    private void loadCourseData() {
        // 初始化控制器
        courseController = new CourseController();

        // 从服务器获取课程数据
        List<Course> courses = courseController.getCourseList();

        if (courses != null && !courses.isEmpty()) {
            DefaultTableModel model = (DefaultTableModel) courseTable.getModel();
            model.setRowCount(0); // 清空现有数据

            for (Course course : courses) {
                model.addRow(new Object[]{
                        course.getCourseId(),
                        course.getCourseName(),
                        course.getCredit(),
                        course.getTeacherName(),
                        course.getTeacherId(),
                        course.getTime(),
                        course.getCapacity(),
                        course.getSelectedNum(),
                        course.getStartWeek(),
                        course.getEndWeek()
                });
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "加载课程数据失败",
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}