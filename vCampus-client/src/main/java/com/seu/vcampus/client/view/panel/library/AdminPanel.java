package com.seu.vcampus.client.view.panel.library;

import com.seu.vcampus.client.service.LibraryService;
import com.seu.vcampus.client.view.panel.library.dialog.AddBookDialog;
import com.seu.vcampus.client.view.panel.library.dialog.EditBookDialog;
import com.seu.vcampus.common.model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class AdminPanel extends JPanel {

    private final LibraryService libraryService = new LibraryService();
    private final DefaultTableModel tableModel;
    private final JTable bookTable;

    private static final String[] COLUMN_NAMES = {"ISBN", "书名", "作者", "出版社", "状态"};

    public AdminPanel() {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        bookTable = new JTable(tableModel);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("图书管理（管理员）", JLabel.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // 按钮区
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("添加图书");
        JButton editButton = new JButton("编辑图书");
        JButton deleteButton = new JButton("删除图书");
        JButton refreshButton = new JButton("刷新");

        addButton.addActionListener(e -> new AddBookDialog(SwingUtilities.getWindowAncestor(this), this::loadAllBooks).setVisible(true));
        editButton.addActionListener(e -> onEditSelected());
        deleteButton.addActionListener(e -> onDeleteSelected());
        refreshButton.addActionListener(e -> loadAllBooks());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 表格区
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        loadAllBooks();
    }

    private void loadAllBooks() {
        List<Book> books = libraryService.getAllBooks();
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

    private void onEditSelected() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一本图书", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String isbn = (String) tableModel.getValueAt(selectedRow, 0);
        Book book = libraryService.getBookByISBN(isbn);
        if (book != null) {
            new EditBookDialog(SwingUtilities.getWindowAncestor(this), book, this::loadAllBooks).setVisible(true);
        }
    }

    private void onDeleteSelected() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一本图书", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String isbn = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除 ISBN 为 " + isbn + " 的图书吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = libraryService.deleteBook(isbn);
            if (success) {
                JOptionPane.showMessageDialog(this, "删除成功");
                loadAllBooks();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}