package com.seu.vcampus.client.view.panel.library.dialog;

import com.seu.vcampus.common.model.Book;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class AddBookDialog extends JDialog {
    private Consumer<Book> onAddCallback;

    private JTextField isbnField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField publisherField;
    private JTextField yearField;
    private JTextField totalCopiesField;
    private JTextField locationField;
    private JTextField imagePathField;
    private JTextArea descriptionArea; // 新增：图书描述文本区域

    public AddBookDialog(Frame owner, Consumer<Book> onAddCallback) {
        super(owner, "添加新书", true);
        this.onAddCallback = onAddCallback;
        setSize(500, 500); // 增加高度以容纳描述区域
        setLocationRelativeTo(owner);
        initComponents();
    }

    private void initComponents() {
        // 使用BorderLayout替代GridLayout，以便更好地组织组件
        setLayout(new BorderLayout(10, 10));

        // 创建表单面板
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));

        // 创建表单字段
        isbnField = new JTextField();
        titleField = new JTextField();
        authorField = new JTextField();
        publisherField = new JTextField();
        yearField = new JTextField();
        totalCopiesField = new JTextField();
        locationField = new JTextField();
        imagePathField = new JTextField();

        // 添加标签和字段
        formPanel.add(new JLabel("ISBN:"));
        formPanel.add(isbnField);
        formPanel.add(new JLabel("书名:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("作者:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("出版社:"));
        formPanel.add(publisherField);
        formPanel.add(new JLabel("出版年份:"));
        formPanel.add(yearField);
        formPanel.add(new JLabel("总数量:"));
        formPanel.add(totalCopiesField);
        formPanel.add(new JLabel("位置:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("图片路径:"));
        formPanel.add(imagePathField);

        // 添加表单面板到对话框中心
        add(formPanel, BorderLayout.NORTH);

        // 新增：图书描述面板
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBorder(BorderFactory.createTitledBorder("图书描述"));
        descriptionPanel.setPreferredSize(new Dimension(0, 120)); // 设置固定高度

        // 创建描述文本区域
        descriptionArea = new JTextArea(5, 40);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("宋体", Font.PLAIN, 14));

        // 添加滚动面板
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);

        // 添加描述面板到对话框底部
        add(descriptionPanel, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton addButton = new JButton("添加");
        addButton.addActionListener(e -> addBook());

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // 添加按钮面板到对话框底部
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addBook() {
        try {
            // 创建图书对象
            Book book = new Book();
            book.setIsbn(isbnField.getText().trim());
            book.setTitle(titleField.getText().trim());
            book.setAuthor(authorField.getText().trim());
            book.setPublisher(publisherField.getText().trim());
            book.setPublishYear(Integer.parseInt(yearField.getText().trim()));
            book.setTotalCopies(Integer.parseInt(totalCopiesField.getText().trim()));
            book.setAvailableCopies(book.getTotalCopies()); // 初始可借数量等于总数量
            book.setLocation(locationField.getText().trim());
            book.setImagePath(imagePathField.getText().trim());
            book.setDescription(descriptionArea.getText().trim()); // 新增：设置描述内容

            // 验证必填字段
            if (book.getIsbn().isEmpty() || book.getTitle().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ISBN和书名不能为空", "错误",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 调用回调函数
            onAddCallback.accept(book);
            dispose(); // 关闭对话框
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "年份和数量必须是数字", "格式错误",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}