package com.seu.vcampus.client.view.panel.library.dialog;

import com.seu.vcampus.common.model.Book;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.function.Consumer;

public class EditBookDialog extends JDialog {
    private Book book;
    private Consumer<Book> onSaveCallback;

    private JTextField isbnField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField publisherField;
    private JSpinner publishYearSpinner;
    private JSpinner totalCopiesSpinner;
    private JSpinner availableCopiesSpinner;
    private JTextField locationField;
    private JTextField imagePathField;
    private JButton saveButton;
    private JButton cancelButton;

    // 新增：图书描述文本区域
    private JTextArea descriptionArea;

    // 图书详情面板
    private JTextArea bookDetailsArea;

    public EditBookDialog(Frame owner, Book book, Consumer<Book> onSaveCallback) {
        super(owner, "修改图书信息", true);
        this.book = book;
        this.onSaveCallback = onSaveCallback;
        initComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setMinimumSize(new Dimension(700, 600)); // 增加高度以容纳描述区域


        // 创建表单面板
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("编辑图书信息"));

        // 创建基本信息的网格布局
        JPanel basicInfoPanel = new JPanel(new GridLayout(9, 2, 10, 10));

        // ISBN（不可编辑）
        basicInfoPanel.add(new JLabel("ISBN:"));
        isbnField = new JTextField(book.getIsbn());
        isbnField.setEditable(false);
        basicInfoPanel.add(isbnField);

        // 书名
        basicInfoPanel.add(new JLabel("书名:"));
        titleField = new JTextField(book.getTitle());
        basicInfoPanel.add(titleField);

        // 作者
        basicInfoPanel.add(new JLabel("作者:"));
        authorField = new JTextField(book.getAuthor());
        basicInfoPanel.add(authorField);

        // 出版社
        basicInfoPanel.add(new JLabel("出版社:"));
        publisherField = new JTextField(book.getPublisher());
        basicInfoPanel.add(publisherField);

        // 出版年份
        basicInfoPanel.add(new JLabel("出版年份:"));
        publishYearSpinner = new JSpinner(new SpinnerNumberModel(
                book.getPublishYear(), 0, Calendar.getInstance().get(Calendar.YEAR), 1));
        basicInfoPanel.add(publishYearSpinner);

        // 总数量
        basicInfoPanel.add(new JLabel("总数量:"));
        totalCopiesSpinner = new JSpinner(new SpinnerNumberModel(
                book.getTotalCopies(), 1, 1000, 1));
        basicInfoPanel.add(totalCopiesSpinner);

        // 可借数量
        basicInfoPanel.add(new JLabel("可借数量:"));
        availableCopiesSpinner = new JSpinner(new SpinnerNumberModel(
                book.getAvailableCopies(), 0, book.getTotalCopies(), 1));
        basicInfoPanel.add(availableCopiesSpinner);

        // 位置
        basicInfoPanel.add(new JLabel("位置:"));
        locationField = new JTextField(book.getLocation());
        basicInfoPanel.add(locationField);

        // 图片路径
        basicInfoPanel.add(new JLabel("图片路径:"));
        imagePathField = new JTextField(book.getImagePath());
        basicInfoPanel.add(imagePathField);

        formPanel.add(basicInfoPanel);

        // 新增：图书描述面板
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBorder(BorderFactory.createTitledBorder("图书描述"));
        descriptionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // 创建描述文本区域
        descriptionArea = new JTextArea(book.getDescription(), 5, 40);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("宋体", Font.PLAIN, 14));

        // 添加滚动面板
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);

        formPanel.add(descriptionPanel);

        // 添加表单到对话框
        add(formPanel, BorderLayout.CENTER);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        saveButton = new JButton("保存");
        cancelButton = new JButton("取消");

        saveButton.addActionListener(e -> saveBook());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

    }


    private void saveBook() {
        // 更新图书对象
        book.setTitle(titleField.getText());
        book.setAuthor(authorField.getText());
        book.setPublisher(publisherField.getText());
        book.setPublishYear((Integer) publishYearSpinner.getValue());
        book.setTotalCopies((Integer) totalCopiesSpinner.getValue());
        book.setAvailableCopies((Integer) availableCopiesSpinner.getValue());
        book.setLocation(locationField.getText());
        book.setImagePath(imagePathField.getText());
        book.setDescription(descriptionArea.getText()); // 新增：保存描述内容

        // 验证数据
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "书名不能为空", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (book.getTotalCopies() <= 0) {
            JOptionPane.showMessageDialog(this, "总数量必须大于0", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (book.getAvailableCopies() < 0 || book.getAvailableCopies() > book.getTotalCopies()) {
            JOptionPane.showMessageDialog(this, "可借数量无效", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 调用回调函数保存图书
        onSaveCallback.accept(book);
        dispose();
    }
}