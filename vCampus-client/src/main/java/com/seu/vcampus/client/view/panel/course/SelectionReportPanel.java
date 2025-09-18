package com.seu.vcampus.client.view.panel.course;

import com.seu.vcampus.client.controller.CourseController;
import com.seu.vcampus.common.model.course.Course;
import com.seu.vcampus.common.model.course.SelectionRecord;
import com.seu.vcampus.common.model.User;
//import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class SelectionReportPanel extends JPanel implements CoursePanel.Refreshable{
    private JTable studentTable;
    private JComboBox<String> courseSelector;
    private User currentUser;
    private CourseController courseController;

    public SelectionReportPanel(User user) {
        this.currentUser = user;
        this.courseController = new CourseController();
        setLayout(new BorderLayout());
        initUI();
    }

    private void initUI() {
        // 顶部面板：课程选择和操作按钮
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 245, 255)); // 浅蓝色背景

        // 课程选择面板
        JPanel coursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        coursePanel.setBorder(BorderFactory.createTitledBorder("课程选择"));

        courseSelector = new JComboBox<>();
        courseSelector.setPreferredSize(new Dimension(300, 30));
        courseSelector.addActionListener(this::loadStudentList);

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadCourseList());

        coursePanel.add(new JLabel("选择课程:"));
        coursePanel.add(courseSelector);
        coursePanel.add(refreshButton);
        topPanel.add(coursePanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // 学生名单表格
        String[] columns = {"学号", "姓名", "院系", "选课时间", "操作"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // 只有"操作"列可编辑
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) {
                    return JButton.class; // 操作列显示按钮
                }
                return Object.class;
            }
        };
        studentTable = new JTable(model);

        // 设置表格样式
        studentTable.setRowHeight(40);
        studentTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        studentTable.setGridColor(new Color(200, 200, 200));
        studentTable.setShowGrid(true);

        // ===== 修复表头问题 =====
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
                setFont(new Font("微软雅黑", Font.BOLD, 16));
                setHorizontalAlignment(JLabel.CENTER);

                // 确保背景绘制
                setOpaque(true);

                return this;
            }
        };

        // 为每个列设置自定义渲染器
        for (int i = 0; i < studentTable.getColumnCount(); i++) {
            studentTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        // ===== 表头修复结束 =====

        // 设置操作列的渲染器和编辑器
        TableColumn operationColumn = studentTable.getColumnModel().getColumn(4);
        operationColumn.setCellRenderer(new ButtonRenderer());
        operationColumn.setCellEditor(new DropButtonEditor(new JCheckBox()));

        // 使用JScrollPane
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        add(scrollPane, BorderLayout.CENTER);

        // 确保表头设置生效
        SwingUtilities.invokeLater(() -> {
            studentTable.getTableHeader().revalidate();
            studentTable.getTableHeader().repaint();
        });
    }

    private void loadCourseList() {
        List<Course> courses = courseController.getCourseList();
        DefaultComboBoxModel<String> fullModel = new DefaultComboBoxModel<>(); // 存储完整列表
        DefaultComboBoxModel<String> filteredModel = new DefaultComboBoxModel<>(); // 用于动态过滤

        // 初始化完整课程列表
        for (Course course : courses) {
            fullModel.addElement(course.getCourseName() + " - " + course.getCourseId());
        }

        courseSelector.setModel(fullModel);
        courseSelector.setEditable(true); // 必须设置为可编辑

        // 获取输入框组件
        JTextField editor = (JTextField) courseSelector.getEditor().getEditorComponent();

        // 监听输入事件，实时过滤
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String keyword = editor.getText().toLowerCase().trim();
                filteredModel.removeAllElements(); // 清空过滤结果

                if (keyword.isEmpty()) {
                    // 如果输入为空，显示空白（不显示任何课程）
                    courseSelector.setModel(new DefaultComboBoxModel<>());
                } else {
                    // 只添加匹配的课程
                    for (int i = 0; i < fullModel.getSize(); i++) {
                        String courseName = fullModel.getElementAt(i);
                        if (courseName.toLowerCase().contains(keyword)) {
                            filteredModel.addElement(courseName);
                        }
                    }
                    courseSelector.setModel(filteredModel);
                }
                courseSelector.showPopup(); // 强制显示下拉框
            }
        });

        if (fullModel.getSize() > 0) {
            loadStudentList(null);
        }
    }

    private void loadStudentList(ActionEvent e) {
        String selectedCourse = (String) courseSelector.getSelectedItem();
        if (selectedCourse == null || selectedCourse.isEmpty()) return;

        String courseId = selectedCourse.split(" - ")[1];

        List<SelectionRecord> records = courseController.getSelectionRecords(courseId,currentUser);
        updateTableWithData(records);
    }

    private void updateTableWithData(List<SelectionRecord> records) {
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0);

        if (records != null && !records.isEmpty()) {
            for (SelectionRecord record : records) {
                model.addRow(new Object[]{
                        record.getStudentId(),
                        record.getStudentName(),
                        record.getDepartment(),
                        record.getSelectionTime(),
                        "退课" // 操作按钮
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
                // 获取当前行的学生ID和课程信息
                String studentId = (String) studentTable.getValueAt(studentTable.getSelectedRow(), 0);
                String courseInfo = (String) courseSelector.getSelectedItem();
                String courseId = courseInfo.split(" - ")[1];

                // 确认退课操作
                int confirm = JOptionPane.showConfirmDialog(button,
                        "确定要退选学生 " + studentId + " 的课程 " + courseInfo + " 吗？",
                        "确认退课",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    // 执行退课操作
                    boolean success = courseController.dropCourseAD(studentId, courseId, currentUser);

                    if (success) {
                        JOptionPane.showMessageDialog(button, "退课成功!");
                        loadStudentList(null); // 刷新数据
                    } else {
                        JOptionPane.showMessageDialog(button, "退课失败，请重试!", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            isPushed = false;
            return label;
        }
    }

    @Override
    public void refreshData() {
        loadCourseList(); // 调用原有的课程列表加载方法
        System.out.println("[DEBUG] 选课统计数据已刷新");
    }
}