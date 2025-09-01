package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.controller.CourseController;
import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class CoursePanel extends JPanel {
    private final User currentUser;
    private final CourseController controller;

    private JTable coursesTable;
    private JTable selectedCoursesTable;
    private JButton refreshButton;
    private JButton selectButton;
    private JButton dropButton;
    private JLabel statusLabel;

    public CoursePanel(User user) {
        this.currentUser = user;
        this.controller = new CourseController();
        this.controller.setCurrentUserId(user.getId());

        initUI();
        loadCourses();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 创建选项卡面板
        JTabbedPane tabbedPane = new JTabbedPane();

        // 添加所有课程面板
        tabbedPane.addTab("所有课程", createAllCoursesPanel());

        // 添加已选课程面板
        tabbedPane.addTab("已选课程", createSelectedCoursesPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // 添加按钮面板
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createAllCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        coursesTable = createCoursesTable();
        JScrollPane scrollPane = new JScrollPane(coursesTable);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSelectedCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        selectedCoursesTable = createCoursesTable();
        JScrollPane scrollPane = new JScrollPane(selectedCoursesTable);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JTable createCoursesTable() {
        String[] columns = {"课程ID", "课程名称", "学分", "授课教师", "时间安排", "容量", "状态"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 所有单元格不可编辑
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.setAutoCreateRowSorter(true);

        // 设置列宽
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // ID
        columnModel.getColumn(1).setPreferredWidth(200); // 名称
        columnModel.getColumn(2).setPreferredWidth(50);  // 学分
        columnModel.getColumn(3).setPreferredWidth(150); // 教师
        columnModel.getColumn(4).setPreferredWidth(150); // 时间
        columnModel.getColumn(5).setPreferredWidth(80);  // 容量
        columnModel.getColumn(6).setPreferredWidth(80);  // 状态

        return table;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        // 刷新按钮
        refreshButton = new JButton("刷新课程列表");
        refreshButton.addActionListener(this::handleRefresh);

        // 选课按钮
        selectButton = new JButton("选课");
        selectButton.addActionListener(this::handleSelectCourse);

        // 退课按钮
        dropButton = new JButton("退课");
        dropButton.addActionListener(this::handleDropCourse);

        // 状态标签
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        panel.add(refreshButton);
        panel.add(selectButton);
        panel.add(dropButton);
        panel.add(statusLabel);

        return panel;
    }

    private void handleRefresh(ActionEvent e) {
        statusLabel.setText("正在刷新课程数据...");
        loadCourses();
    }

    private void handleSelectCourse(ActionEvent e) {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一门课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseId = (String) coursesTable.getValueAt(selectedRow, 0);
        String courseName = (String) coursesTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要选择课程: " + courseName + " (" + courseId + ")?",
                "确认选课",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        statusLabel.setText("正在选择课程: " + courseName + "...");

        // 异步执行选课操作
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return controller.selectCourse(courseId);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        statusLabel.setText("选课成功");
                        JOptionPane.showMessageDialog(
                                CoursePanel.this,
                                "成功选择课程: " + courseName,
                                "选课成功",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        loadCourses();
                    } else {
                        statusLabel.setText("选课失败");
                        JOptionPane.showMessageDialog(
                                CoursePanel.this,
                                "选课失败，请重试或联系管理员",
                                "选课失败",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (Exception ex) {
                    statusLabel.setText("系统错误");
                    JOptionPane.showMessageDialog(
                            CoursePanel.this,
                            "系统错误: " + ex.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void handleDropCourse(ActionEvent e) {
        int selectedRow = selectedCoursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一门已选课程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseId = (String) selectedCoursesTable.getValueAt(selectedRow, 0);
        String courseName = (String) selectedCoursesTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "确定要退选课程: " + courseName + " (" + courseId + ")?",
                "确认退课",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        statusLabel.setText("正在退选课程: " + courseName + "...");

        // 异步执行退课操作
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return controller.dropCourse(courseId);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        statusLabel.setText("退课成功");
                        JOptionPane.showMessageDialog(
                                CoursePanel.this,
                                "成功退选课程: " + courseName,
                                "退课成功",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        loadCourses();
                    } else {
                        statusLabel.setText("退课失败");
                        JOptionPane.showMessageDialog(
                                CoursePanel.this,
                                "退课失败，请重试或联系管理员",
                                "退课失败",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (Exception ex) {
                    statusLabel.setText("系统错误");
                    JOptionPane.showMessageDialog(
                            CoursePanel.this,
                            "系统错误: " + ex.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }.execute();
    }

    private void loadCourses() {
        // 异步加载课程数据
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                loadAllCourses();
                loadSelectedCourses();
                return null;
            }

            @Override
            protected void done() {
                statusLabel.setText("数据加载完成");
            }
        }.execute();
    }

    private void loadAllCourses() {
        List<Course> courses = controller.getAllCourses();
        updateCoursesTable(coursesTable, courses);
    }

    private void loadSelectedCourses() {
        List<Course> courses = controller.getSelectedCourses();
        updateCoursesTable(selectedCoursesTable, courses);
    }

    private void updateCoursesTable(JTable table, List<Course> courses) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // 清空表格

        for (Course course : courses) {
            Object[] row = {
                    course.getCourseId(),
                    course.getCourseName(),
                    course.getCredit(),
                    course.getTeacherId(),
                    course.getSchedule(),
                    course.getSelected() + "/" + course.getCapacity(),
                    determineCourseStatus(course, table == selectedCoursesTable)
            };
            model.addRow(row);
        }
    }

    private String determineCourseStatus(Course course, boolean isSelected) {
        if (isSelected) {
            return "已选";
        }

        if (course.getSelected() >= course.getCapacity()) {
            return "已满";
        }

        return "可选";
    }
}