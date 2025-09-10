package com.seu.vcampus.client.view.panel.library.dialog;

import com.seu.vcampus.client.service.LibraryService;
import com.seu.vcampus.common.model.Book;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AddBookDialog extends JDialog {
    private final LibraryService libraryService = new LibraryService();
    private final Runnable onAddSuccess;
    private JTextField isbnField, titleField, authorField, publisherField;

    public AddBookDialog(Window parent, Runnable onAddSuccess) {
        super((Frame) parent, "添加图书", true);
        this.onAddSuccess = onAddSuccess;
        initializeUI();
        setupCloseListener();
    }

    private void initializeUI() {
        setLayout(new GridLayout(5, 2, 10, 10));
        setPreferredSize(new Dimension(400, 200));

        add(new JLabel("ISBN：")); add(isbnField = new JTextField());
        add(new JLabel("书名：")); add(titleField = new JTextField());
        add(new JLabel("作者：")); add(authorField = new JTextField());
        add(new JLabel("出版社：")); add(publisherField = new JTextField());

        JButton addButton = new JButton("添加");
        JButton cancelButton = new JButton("取消");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        add(new JLabel()); add(buttonPanel);

        pack();
        setLocationRelativeTo(getParent());

        addButton.addActionListener(e -> addBook());
        cancelButton.addActionListener(e -> dispose());
    }

    private void addBook() {
        String isbn = isbnField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String publisher = publisherField.getText().trim();

        if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || publisher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段均为必填", "输入错误", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setAvailableCopies(1);

        boolean success = libraryService.addBook(book);
        if (success) {
            JOptionPane.showMessageDialog(this, "图书添加成功！");
            onAddSuccess.run();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "添加失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
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