package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.controller.CourseController;
import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.ResponseCode;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class CourseSelectionPanel extends JPanel implements CoursePanel.Refreshable{
    private JTable selectedCoursesTable;
    private CourseController courseController;
    private User currentUser;

    public CourseSelectionPanel(User user) {
        this.currentUser = user;
        this.courseController = new CourseController();
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // 创建表格模型 - 移除状态列
        String[] columns = {"课程ID", "课程名称", "学分", "授课教师", "时间安排", "地点", "操作"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // 只有"操作"列可编辑
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) {
                    return JButton.class; // 操作列显示按钮
                }
                return Object.class;
            }
        };

        selectedCoursesTable = new JTable(model);

        // 设置表格样式
        selectedCoursesTable.setRowHeight(30);
        selectedCoursesTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        selectedCoursesTable.setSelectionBackground(new Color(173, 216, 230));

        // 表头样式
        JTableHeader header = selectedCoursesTable.getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);

        // 设置操作列的渲染器和编辑器
        TableColumn operationColumn = selectedCoursesTable.getColumnModel().getColumn(6);
        operationColumn.setCellRenderer(new ButtonRenderer());
        operationColumn.setCellEditor(new DropButtonEditor(new JCheckBox()));

        // ============== 底部操作栏 ==============
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // 状态标签
        JLabel statusLabel = new JLabel("学生: " + currentUser.getName() + "(" + currentUser.getId() + ")");
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton refreshButton = new JButton("刷新数据");
        refreshButton.setPreferredSize(new Dimension(100, 30));
        refreshButton.addActionListener(e -> loadSelectedCourses());

        buttonPanel.add(refreshButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // ============== 组装界面 ==============
        add(new JScrollPane(selectedCoursesTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadSelectedCourses() {
        // 从数据库获取已选课程
        List<Course> selectedCourses = courseController.getCoursesByStudentId(currentUser.getId(),currentUser);
        updateTableWithData(selectedCourses);
    }

    private void updateTableWithData(List<Course> courses) {
        DefaultTableModel model = (DefaultTableModel) selectedCoursesTable.getModel();
        model.setRowCount(0); // 清空现有数据

        if (courses != null && !courses.isEmpty()) {
            for (Course course : courses) {
                // 添加行数据 - 移除状态列
                model.addRow(new Object[]{
                        course.getCourseId(),      // 课程ID
                        course.getCourseName(),    // 课程名称
                        course.getCredit(),        // 学分
                        course.getTeacherName(),   // 授课教师
                        course.getSchedule(),      // 时间安排
                        course.getLocation(),      // 地点
                        "退课"                     // 操作列按钮
                });
            }
        }
    }

    // 按钮渲染器
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // 退课按钮编辑器
    class DropButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public DropButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // 获取当前行的课程ID和课程名称
                String courseId = (String) selectedCoursesTable.getValueAt(selectedCoursesTable.getSelectedRow(), 0);
                String courseName = (String) selectedCoursesTable.getValueAt(selectedCoursesTable.getSelectedRow(), 1);

                // 确认退课操作
                int confirm = JOptionPane.showConfirmDialog(button,
                        "确定要退选课程《" + courseName + "》吗？",
                        "确认退课",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    // 执行退课操作
                    boolean success = courseController.dropCourse(currentUser.getId(), courseId,currentUser);

                    if (success) {
                        JOptionPane.showMessageDialog(button, "退课成功!");
                        loadSelectedCourses(); // 刷新数据
                    } else {
                        JOptionPane.showMessageDialog(button, "退课失败，请重试!", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    @Override
    public void refreshData() {
        loadSelectedCourses(); // 调用原有的数据加载方法
        System.out.println("[DEBUG] 选课数据已刷新");
    }
}