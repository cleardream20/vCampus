package com.seu.vcampus.client.view.panel.library.dialog;

import com.seu.vcampus.client.service.LibraryService;
import com.seu.vcampus.common.model.Book;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BookDetailDialog extends JDialog {
    private final LibraryService libraryService = new LibraryService();
    private final Book book;
    private final String userId;

    public BookDetailDialog(Window parent, Book book, String userId) {
        super((Frame) parent, "图书详情", true);
        this.book = book;
        this.userId = userId;
        initializeUI();
        setupCloseListener();
    }

    private boolean isAvailable(Book book) {
        return book.getAvailableCopies() > 0;
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));

        // 信息展示
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(new JLabel("ISBN：")); infoPanel.add(new JLabel(book.getIsbn()));
        infoPanel.add(new JLabel("书名：")); infoPanel.add(new JLabel(book.getTitle()));
        infoPanel.add(new JLabel("作者：")); infoPanel.add(new JLabel(book.getAuthor()));
        infoPanel.add(new JLabel("出版社：")); infoPanel.add(new JLabel(book.getPublisher()));
        infoPanel.add(new JLabel("状态："));
        JLabel statusLabel = new JLabel(isAvailable(book) ? "可借" : "已借出");
        statusLabel.setForeground(isAvailable(book) ? Color.GREEN : Color.RED);
        infoPanel.add(statusLabel);

        add(infoPanel, BorderLayout.CENTER);

        // 操作按钮
        JPanel buttonPanel = new JPanel();
        if (isAvailable(book)) {
            JButton borrowBtn = new JButton("借阅");
            borrowBtn.addActionListener(e -> borrowBook());
            buttonPanel.add(borrowBtn);
        } else {
            buttonPanel.add(new JLabel("该书已被借出"));
        }
        JButton closeBtn = new JButton("关闭");
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    private void borrowBook() {
        int confirm = JOptionPane.showConfirmDialog(this, "确定要借阅《" + book.getTitle() + "》吗？", "确认借阅", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = libraryService.borrowBook(userId, book.getIsbn());
            if (success) {
                JOptionPane.showMessageDialog(this, "借阅成功！");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "借阅失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setupCloseListener() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }
}