package com.seu.vcampus.client.view.panel.student;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.swing.filechooser.FileNameExtensionFilter;


import com.seu.vcampus.client.service.StudentService;
import com.seu.vcampus.client.view.NavigatablePanel;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.model.User;

public class ADPanel extends JPanel implements NavigatablePanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private TableRowSorter<TableModel> sorter;
    private JPanel northPanel;
    private final int[] columnWidths = {100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100}; // 列宽数组
    private HashMap<Integer, String> filters = new HashMap<>();
    private final String[] columnNames = new String[] {"一卡通号","身份证号","学号","姓名","性别","电话号码","出生日期","家庭住址","入学日期","学籍号","学院","年级","学制","学籍状态"};
    private StudentService service;

    public ADPanel() {
        setLayout(new BorderLayout());

        service = new StudentService();
        // 创建北部面板，使用垂直BoxLayout
        northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        initToolbar();
        initTable();
        initStatusBar();
        setupKeyboardShortcuts();

        // 将北部面板添加到主布局
        add(northPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        // 初始时不加载数据，表格为空


        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // 设置表格自动调整模式，启用水平滚动
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // 设置列宽 - 对于宽表格，设置合适的列宽
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnWidths.length && i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        // 设置排序器
        sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);

        // 添加选择监听器更新状态栏
        table.getSelectionModel().addListSelectionListener(e -> updateStatusBar());

        // 创建滚动面板并同时启用水平和垂直滚动
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void initToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // 限制高度

        // 添加返回按钮
        JButton backButton = createStyledButton("返回");
        backButton.addActionListener(e -> returnToPrevious());

        JButton queryButton = createStyledButton("查询数据");
        queryButton.addActionListener(e -> loadDataWithFilters());

        JButton filterButton = createStyledButton("筛选条件");
        filterButton.addActionListener(e -> showFilterDialog());

        JButton copyButton = createStyledButton("复制选中行");
        copyButton.addActionListener(e -> copySelectedRows());

        JButton clearButton = createStyledButton("清除选择");
        clearButton.addActionListener(e -> table.clearSelection());

        JButton exportButton = createStyledButton("导出到文件");
        exportButton.addActionListener(e -> exportToFile());

        // 将所有按钮添加到工具栏
        toolBar.add(backButton);
        toolBar.add(queryButton);
        toolBar.add(filterButton);
        toolBar.add(copyButton);
        toolBar.add(clearButton);
        toolBar.add(exportButton);

        northPanel.add(toolBar);
    }

    // 返回上一级
    private void returnToPrevious() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.dispose();
        }
    }

    // 创建统一样式的按钮
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setMargin(new Insets(3, 8, 3, 8));
        return button;
    }

    private void initStatusBar() {
        statusLabel = new JLabel("就绪 - 请点击'查询数据'按钮加载数据");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void setupKeyboardShortcuts() {
        // 设置Ctrl+A全选快捷键
        KeyStroke ctrlA = KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK);
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ctrlA, "selectAll");
        table.getActionMap().put("selectAll", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.selectAll();
            }
        });
    }

    private void showFilterDialog(){
        JDialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "筛选条件", true);
        filterDialog.setLayout(new BorderLayout());
        filterDialog.setSize(500, 400);
        filterDialog.setLocationRelativeTo(this);

        // 创建面板放置筛选条件
        JPanel filterPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Map<Integer, JTextField> dialogFilterFields = new HashMap<>();

        for (int i = 0; i < columnNames.length; i++) {
            JLabel label = new JLabel(columnNames[i] + ":");
            JTextField textField = new JTextField();

            // 如果已有筛选条件，则填充
            if (filters.containsKey(i)) {
                textField.setText(filters.get(i));
            }

            dialogFilterFields.put(i, textField);
            filterPanel.add(label);
            filterPanel.add(textField);
        }

        JScrollPane scrollPane = new JScrollPane(filterPanel);
        filterDialog.add(scrollPane, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");

        okButton.addActionListener(e -> {
            // 收集筛选条件
            filters.clear();
            for (Map.Entry<Integer, JTextField> entry : dialogFilterFields.entrySet()) {
                String filterText = entry.getValue().getText().trim();
                if (!filterText.isEmpty()) {
                    filters.put(entry.getKey(), filterText);
                }
            }
            filterDialog.dispose();

            // 自动触发筛选
            loadDataWithFilters();
        });

        cancelButton.addActionListener(e -> filterDialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);

        filterDialog.setVisible(true);
    }

    private void loadDataWithFilters(){
        // 从数据库获取筛选后的数据
        List<Student> filteredData = new ArrayList<>();
        try {
            service.getDataWithFilters(filters);

            // 更新表格模型
            tableModel.setRowCount(0); // 清空现有数据
            for (Student student : filteredData) {
                Object[] row = student.getRow();
                tableModel.addRow(row);
            }

            // 更新状态栏
            updateStatusBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copySelectedRows() {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "请先选择要复制的行", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();

        // 添加表头
        for (int i = 0; i < table.getColumnCount(); i++) {
            sb.append(table.getColumnName(i));
            if (i < table.getColumnCount() - 1) {
                sb.append("\t");
            }
        }
        sb.append("\n");

        // 添加选中行的数据
        for (int viewRow : selectedRows) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            for (int col = 0; col < table.getColumnCount(); col++) {
                Object value = tableModel.getValueAt(modelRow, col);
                sb.append(value != null ? value.toString() : "");
                if (col < table.getColumnCount() - 1) {
                    sb.append("\t");
                }
            }
            sb.append("\n");
        }

        // 复制到剪贴板
        StringSelection stringSelection = new StringSelection(sb.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        JOptionPane.showMessageDialog(this, "已复制 " + selectedRows.length + " 行数据到剪贴板",
                "成功", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportToFile() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "没有数据可导出", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出表格数据");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV文件", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // 确保文件扩展名是.csv
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                // 写入表头
                for (int i = 0; i < table.getColumnCount(); i++) {
                    writer.write(table.getColumnName(i));
                    if (i < table.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.newLine();

                // 写入数据
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object value = tableModel.getValueAt(row, col);
                        String output = value != null ? value.toString() : "";
                        // 处理可能包含逗号的值
                        if (output.contains(",")) {
                            output = "\"" + output + "\"";
                        }
                        writer.write(output);
                        if (col < tableModel.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.newLine();
                }

                JOptionPane.showMessageDialog(this, "数据已成功导出到: " + fileToSave.getAbsolutePath(),
                        "导出成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "导出文件时出错: " + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStatusBar() {
        int rowCount = tableModel.getRowCount();
        int selectedCount = table.getSelectedRows().length;

        if (rowCount == 0) {
            statusLabel.setText("没有数据 - 请点击'查询数据'按钮加载数据");
        } else if (selectedCount > 0) {
            statusLabel.setText("已加载 " + rowCount + " 行数据，已选择 " + selectedCount + " 行");
        } else {
            statusLabel.setText("已加载 " + rowCount + " 行数据");
        }

        // 如果有筛选条件，显示在状态栏
        if (!filters.isEmpty()) {
            statusLabel.setText(statusLabel.getText() + " (已应用筛选)");
        }
    }


    @Override
    public void refreshPanel(User user) {
        // 刷新面板逻辑
    }

    @Override
    public String getPanelName() {
        return "StudentAD";
    }

    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(new ADPanel());
        frame.setSize(800,600);
    }
}