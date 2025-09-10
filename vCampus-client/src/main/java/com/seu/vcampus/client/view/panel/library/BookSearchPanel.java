package com.seu.vcampus.client.view.panel.library;

import com.seu.vcampus.client.service.LibraryService;
import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.client.view.panel.library.dialog.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class BookSearchPanel extends JPanel {

    private final String userId;
    private final LibraryService libraryService = new LibraryService();
    private final JTextField searchField = new JTextField(20);
    private final JTable bookTable;
    private final DefaultTableModel tableModel;

    private static final String[] COLUMN_NAMES = {"ISBN", "书名", "作者", "出版社", "状态"};

    public BookSearchPanel(String userId) {
        this.userId = userId;
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        bookTable = new JTable(tableModel);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // 搜索区域
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("关键词："));
        searchPanel.add(searchField);
        JButton searchButton = new JButton("搜索");
        searchButton.addActionListener(this::onSearch);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // 表格区域
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        // 双击查看详情
        bookTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = bookTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String isbn = (String) tableModel.getValueAt(selectedRow, 0);
                        showBookDetail(isbn);
                    }
                }
            }
        });

        // 默认加载全部图书
        loadAllBooks();
    }

    private void onSearch(ActionEvent e) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllBooks();
        } else {
            List<Book> books = libraryService.searchBooks(keyword);
            updateTableData(books);
        }
    }

    private void loadAllBooks() {
        List<Book> books = libraryService.getAllBooks();
        updateTableData(books);
    }

    private void updateTableData(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.isAvailable() ? "可借" : "已借出"
            });
        }
    }

    private void showBookDetail(String isbn) {
        Book book = libraryService.getBookByISBN(isbn);
        if (book != null) {
            new BookDetailDialog(SwingUtilities.getWindowAncestor(this), book, userId).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "无法加载图书信息", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}