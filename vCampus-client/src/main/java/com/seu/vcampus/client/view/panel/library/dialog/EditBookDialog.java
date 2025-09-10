package com.seu.vcampus.client.view.panel.library.dialog;

import com.seu.vcampus.client.service.LibraryService;
import com.seu.vcampus.common.model.Book;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EditBookDialog extends JDialog {
    private final LibraryService libraryService = new LibraryService();
    private final Book book;
    private final Runnable onUpdateSuccess;
    private JTextField titleField, authorField, publisherField;

    public EditBookDialog(Window parent, Book book, Runnable onUpdateSuccess) {
        super((Frame) parent, "编辑图书", true);
        this.book = book;
        this.onUpdateSuccess = onUpdateSuccess;
        initializeUI();
        setupCloseListener();
    }

    private void initializeUI() {
        setLayout(new GridLayout(4, 2, 10, 10));
        setPreferredSize(new Dimension(400, 180));

        add(new JLabel("书名：")); add(titleField = new JTextField(book.getTitle()));
        add(new JLabel("作者：")); add(authorField = new JTextField(book.getAuthor()));
        add(new JLabel("出版社：")); add(publisherField = new JTextField(book.getPublisher()));

        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(new JLabel()); add(buttonPanel);

        pack();
        setLocationRelativeTo(getParent());

        saveButton.addActionListener(e -> updateBook());
        cancelButton.addActionListener(e -> dispose());
    }

    private void updateBook() {
        book.setTitle(titleField.getText().trim());
        book.setAuthor(authorField.getText().trim());
        book.setPublisher(publisherField.getText().trim());

        boolean success = libraryService.updateBook(book);
        if (success) {
            JOptionPane.showMessageDialog(this, "更新成功！");
            onUpdateSuccess.run();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "更新失败", "错误", JOptionPane.ERROR_MESSAGE);
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