package com.seu.vcampus.client.view.panel.course;

import com.seu.vcampus.client.controller.CourseController;
import com.seu.vcampus.common.model.course.Course;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Message;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CourseQueryPanel extends JPanel implements CoursePanel.Refreshable{
    private JTable courseTable;
    private JTextField searchField;
    private JComboBox<String> searchTypeComboBox;
    private User currentUser;
    private CourseController courseController;

    public CourseQueryPanel(User user) {
        this.currentUser = user;
        this.courseController = new CourseController();
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // 创建顶部面板（包含搜索栏）
        JPanel topPanel = new JPanel(new BorderLayout());

        // 搜索面板
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("课程搜索"));

        // 搜索类型选择
        String[] searchTypes = {"课程ID", "课程名称"};
        searchTypeComboBox = new JComboBox<>(searchTypes);
        searchTypeComboBox.setSelectedIndex(0);

        // 搜索输入框
        searchField = new JTextField(20);

        // 搜索按钮
        JButton searchButton = new JButton("搜索");
        searchButton.addActionListener(this::performSearch);

        // 刷新按钮
        JButton refreshButton = new JButton("刷新数据");
        refreshButton.addActionListener(e -> loadCourseData());

        // 添加组件到搜索面板
        searchPanel.add(new JLabel("搜索方式:"));
        searchPanel.add(searchTypeComboBox);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        // 将搜索面板添加到顶部面板
        topPanel.add(searchPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.NORTH);

        // 初始化表格 - 添加所有要求的列，包括"操作"列
        String[] columns = {"课程ID", "课程名称", "学分", "授课教师", "开课学院", "时间安排", "容量", "已选人数", "开始周", "结束周", "操作"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 10; // 只有"操作"列可编辑（用于放置按钮）
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 10) {
                    return JButton.class; // 操作列显示按钮
                }
                return Object.class;
            }
        };
        courseTable = new JTable(model);

        // 设置表格样式
        courseTable.setRowHeight(30);
        courseTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 设置表头渲染器 - 关键修复
        JTableHeader header = courseTable.getTableHeader();

        // 创建自定义表头渲染器
        TableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // 固定设置背景和前景色
                setBackground(new Color(70, 130, 180)); // 钢蓝色
                setForeground(Color.WHITE);             // 白色文字

                // 设置字体和居中对齐
                setFont(new Font("微软雅黑", Font.BOLD, 14));
                setHorizontalAlignment(JLabel.CENTER);

                // 确保背景绘制
                setOpaque(true);

                return this;
            }
        };

        // 为每个列设置自定义渲染器
        for (int i = 0; i < courseTable.getColumnCount(); i++) {
            courseTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // 设置操作列的渲染器和编辑器
        TableColumn operationColumn = courseTable.getColumnModel().getColumn(10);
        operationColumn.setCellRenderer(new ButtonRenderer());
        operationColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

        add(new JScrollPane(courseTable), BorderLayout.CENTER);

        // 确保表头设置生效
        SwingUtilities.invokeLater(() -> {
            header.revalidate();
            header.repaint();
        });
    }

    private void performSearch(ActionEvent e) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadCourseData(); // 如果搜索框为空，加载所有课程
            return;
        }

        int searchType = searchTypeComboBox.getSelectedIndex();
        List<Course> searchResults = null;

        if (searchType == 0) { // 按课程ID搜索
            Course course = courseController.getCourseById(keyword);
            if (course != null) {
                searchResults = List.of(course);
            }
        } else { // 按课程名称搜索
            searchResults = courseController.getCourseByName(keyword);
        }

        updateTableWithData(searchResults);
    }

    private void updateTableWithData(List<Course> courses) {
        DefaultTableModel model = (DefaultTableModel) courseTable.getModel();
        model.setRowCount(0); // 清空现有数据

        if (courses != null && !courses.isEmpty()) {
            for (Course course : courses) {
                // 添加所有要求的列数据
                model.addRow(new Object[]{
                        course.getCourseId(),
                        course.getCourseName(),
                        course.getCredit(),
                        course.getTeacherName(),
                        course.getDepartment(), // 开课学院
                        course.getSchedule(),
                        course.getCapacity(),
                        course.getSelectedNum(), // 已选人数
                        course.getStartWeek(),   // 开始周
                        course.getEndWeek(),     // 结束周
                        "选择"                   // 操作列按钮
                });
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "未找到匹配的课程",
                    "搜索结果",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadCourseData() {
        // 从服务器获取课程数据
        List<Course> courses = courseController.getCourseList();
        updateTableWithData(courses);
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

    // 按钮编辑器
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
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
                // 获取当前行的课程ID
                String courseId = (String) courseTable.getValueAt(courseTable.getSelectedRow(), 0);

                try {
                    // 执行选课操作
                    Message response = courseController.selectCourse(currentUser.getCid(), courseId,currentUser);

                    if (Message.STATUS_SUCCESS.equals(response.getStatus())) {
                        JOptionPane.showMessageDialog(button, "选课成功!");
                        loadCourseData(); // 刷新数据
                    } else {
                        // 显示具体的错误原因
                        String errorMessage = "选课失败: " + response.getMessage();
                        JOptionPane.showMessageDialog(button, errorMessage, "选课失败", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    // 处理异常情况
                    JOptionPane.showMessageDialog(button, "选课操作异常: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
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
        loadCourseData(); // 调用原有的数据加载方法
        System.out.println("[DEBUG] 课程查询数据已刷新");
    }
}