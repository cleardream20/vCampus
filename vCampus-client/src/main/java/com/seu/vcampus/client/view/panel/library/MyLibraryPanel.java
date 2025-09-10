// 文件：com/seu/vcampus/client/view/panel/library/MyLibraryPanel.java
package com.seu.vcampus.client.view.panel.library;

import com.seu.vcampus.client.service.LibraryService;
import com.seu.vcampus.common.model.BorrowRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyLibraryPanel extends JPanel {

    private final String userId;
    private final LibraryService libraryService = new LibraryService();
    private final DefaultTableModel tableModel;
    private final JTable recordTable;

    private static final String[] COLUMN_NAMES = {"记录ID", "ISBN", "书名", "借阅日期", "应还日期", "状态"};
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MyLibraryPanel(String userId) {
        this.userId = userId;
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        recordTable = new JTable(tableModel);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("我的借阅记录", JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // 表格
        JScrollPane scrollPane = new JScrollPane(recordTable);
        add(scrollPane, BorderLayout.CENTER);

        // 操作按钮
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> loadRecords());
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        loadRecords(); // 初始化加载
    }

    private void loadRecords() {
        List<BorrowRecord> records = libraryService.getBorrowRecordsByUserId(userId);
        tableModel.setRowCount(0);
        for (BorrowRecord record : records) {
            tableModel.addRow(new Object[]{
                    record.getBookIsbn(),
                    record.getBookTitle(),
                    record.getBorrowDate(),
                    record.getDueDate(),
                    record.getStatus()
            });
        }
    }
}