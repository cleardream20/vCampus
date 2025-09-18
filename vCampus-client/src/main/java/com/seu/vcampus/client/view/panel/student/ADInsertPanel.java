package com.seu.vcampus.client.view.panel.student;

import com.seu.vcampus.client.service.StudentService;
import com.seu.vcampus.common.model.Student;

import java.awt.Font;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;


public class ADInsertPanel extends JFrame {
    private JPanel mainPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private final StudentService studentService = new StudentService();
    private final String[] columnNames = new String[] {"姓名","电话","邮箱","性别","年龄","出生日期","家庭住址","身份证号","入学日期","年级","专业","学籍号","学制","学籍状态"};

    public ADInsertPanel() {
        // 设置窗口属性
        setTitle("添加学生");
        setSize(800, 600);
        setLocationRelativeTo(null); // 居中显示

        // 创建主面板，使用BorderLayout
        mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // 创建左侧导航栏
        JPanel sidebarPanel = createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // 创建右侧内容区域，使用CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // 添加内容面板
        contentPanel.add(ManualInsertPanel(), "Dashboard");
        contentPanel.add(ExcelInsertPanel(), "Settings");

        // 默认显示第一个面板
        cardLayout.show(contentPanel, "Dashboard");
    }

    // 创建左侧导航栏
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(150, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // 创建按钮
        JButton dashboardBtn = createSidebarButton("手动添加");
        JButton settingsBtn = createSidebarButton("从文件导入");

        // 添加按钮点击事件
        dashboardBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "Dashboard");
            }
        });

        settingsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "Settings");
            }
        });

        // 添加按钮到侧边栏
        sidebar.add(dashboardBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10))); // 按钮间距
        sidebar.add(settingsBtn);
        sidebar.add(Box.createVerticalGlue()); // 将按钮推到顶部

        return sidebar;
    }

    // 创建侧边栏按钮样式
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setMaximumSize(new Dimension(120, 40));
        button.setMargin(new Insets(3, 8, 3, 8));
        return button;
    }

    // 创建控制面板内容
    private JPanel ManualInsertPanel() {
        // 创建面板放置筛选条件
        JPanel filterPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Map<Integer, JTextField> dialogFilterFields = new HashMap<>();

        for (int i = 0; i < columnNames.length; i++) {
            JLabel label = new JLabel(columnNames[i] + ":");
            JTextField textField = new JTextField();

            dialogFilterFields.put(i, textField);
            filterPanel.add(label);
            filterPanel.add(textField);
        }

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("确定");

        Map<Integer, String> filterFileds = new HashMap<>();
        okButton.addActionListener(e -> {
            List<Student> newStudents = new ArrayList<>();
            for(Map.Entry<Integer, JTextField> entry : dialogFilterFields.entrySet()) {
                if(entry.getValue().getText().isEmpty()) {
                    JDialog dialog = new JDialog(this, "error", true);
                    dialog.setSize(300, 200);
                    dialog.setLocationRelativeTo(this);
                    JButton closeButton = new JButton("关闭");
                    closeButton.setFocusPainted(false);
                    closeButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dialog.dispose(); // 关闭对话框
                        }
                    });
                    JLabel label = new JLabel("存在信息为空", SwingConstants.CENTER);
                    dialog.setLayout(new BorderLayout());
                    dialog.add(label, BorderLayout.CENTER);
                    dialog.add(closeButton, BorderLayout.SOUTH);
                    dialog.setVisible(true);
                    return;
                }
                filterFileds.put(entry.getKey(), entry.getValue().getText());
            }
            newStudents.add(new Student(filterFileds));
            addStudent(newStudents);
        });
        buttonPanel.add(okButton);
        filterPanel.add(buttonPanel);

        return filterPanel;
    }

    public void addStudent(List<Student> studentList) {
        try {
            studentService.addStudent(studentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 创建系统设置内容
    private JPanel ExcelInsertPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建文件选择区域
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel fileLabel = new JLabel("选择Excel文件:");
        JTextField filePathField = new JTextField(20);
        filePathField.setEditable(false);
        JButton browseButton = new JButton("浏览");

        filePanel.add(fileLabel);
        filePanel.add(filePathField);
        filePanel.add(browseButton);

        // 创建表格
        JTable dataTable = new JTable();
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane tableScrollPane = new JScrollPane(dataTable);

        // 创建按钮区域
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("确认导入");
        confirmButton.setEnabled(false);
        buttonPanel.add(confirmButton);

        // 添加到主面板
        panel.add(filePanel, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 浏览按钮事件处理
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Excel文件", "xlsx", "xls"));

            int result = fileChooser.showOpenDialog(ADInsertPanel.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());

                try {
                    // 读取Excel文件
                    List<Student> students = readExcelFile(selectedFile);
                    updateTable(dataTable, students);

                    // 启用确认按钮
                    confirmButton.setEnabled(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ADInsertPanel.this,
                            "读取Excel文件时出错: " + ex.getMessage(),
                            "错误", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        // 确认按钮事件处理
        confirmButton.addActionListener(e -> {
            TableModel model = dataTable.getModel();
            List<Student> students = new ArrayList<>();

            // 从表格模型提取数据
            for (int i = 0; i < model.getRowCount(); i++) {
                Map<Integer, String> fieldMap = new HashMap<>();
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    fieldMap.put(j, value != null ? value.toString() : "");
                }

                // 创建Student对象
                students.add(new Student(fieldMap));
            }

            // 添加学生
            addStudent(students);
            JOptionPane.showMessageDialog(ADInsertPanel.this,
                    "成功导入 " + students.size() + " 名学生数据",
                    "成功", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private List<Student> readExcelFile(File file) throws Exception {
        List<Student> students = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
            Iterator<Row> rowIterator = sheet.iterator();

            // 跳过标题行（如果有）
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            // 遍历行
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<Integer, String> fieldMap = new HashMap<>();

                // 遍历列
                String value;
                for (int i = 0; i < columnNames.length; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if(i == 4) {
                        DataFormatter formatter = new DataFormatter();
                        value = formatter.formatCellValue(cell);
                    } else {
                        value = cell.getStringCellValue();
                    }

                    fieldMap.put(i, value);
                }

                students.add(new Student(fieldMap));
            }
        }

        return students;
    }

    // 更新表格数据
    private void updateTable(JTable table, List<Student> students) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Student student : students) {
            model.addRow(student.getInsertRow());
        }
        table.setModel(model);
    }

    public static void main(String[] args) {
        // 使用SwingUtilities确保GUI线程安全
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ADInsertPanel().setVisible(true);
            }
        });
    }
}