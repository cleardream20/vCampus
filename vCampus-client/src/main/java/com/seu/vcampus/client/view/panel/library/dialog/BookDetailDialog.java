package com.seu.vcampus.client.view.panel.library.dialog;

import com.seu.vcampus.client.controller.LibraryController;
import com.seu.vcampus.client.service.LibraryService;
import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BookDetailDialog extends JDialog {
    private Book book;
    private LibraryService libraryService;
    private User currentUser;

    public BookDetailDialog(Frame owner, Book book, LibraryService libraryService, User currentUser) {
        super(owner, "图书详情", true);
        this.book = book;
        this.libraryService = libraryService;
        this.currentUser= currentUser;
        setSize(600, 500);
        setLocationRelativeTo(owner);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        // 图片面板
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createTitledBorder("封面"));
        // 加载图片
        ImageIcon bookImage = ImageLoader.loadImage(book.getImagePath());
        // 缩放图片
        Image scaledImage = bookImage.getImage().getScaledInstance(200, 260, Image.SCALE_SMOOTH);
        bookImage = new ImageIcon(scaledImage);

        JLabel imageLabel = new JLabel(bookImage);
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePanel.add(imageLabel, BorderLayout.CENTER);

        // 信息面板
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("图书信息"));

        addInfoRow(infoPanel, "ISBN:", book.getIsbn());
        addInfoRow(infoPanel, "书名:", book.getTitle());
        addInfoRow(infoPanel, "作者:", book.getAuthor());
        addInfoRow(infoPanel, "出版社:", book.getPublisher());
        addInfoRow(infoPanel, "出版年份:", String.valueOf(book.getPublishYear()));

        // 可借数量行（特殊样式）
        JLabel copiesLabel = new JLabel(String.valueOf(book.getAvailableCopies()));
        copiesLabel.setForeground(book.getAvailableCopies() > 0 ? Color.BLACK : Color.RED);
        addInfoRowWithComponent(infoPanel, "可借数量:", copiesLabel);

        addInfoRow(infoPanel, "位置:", book.getLocation());

        // 状态标签
        JLabel statusLabel = new JLabel();
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        updateStatusLabel(statusLabel);
        addInfoRowWithComponent(infoPanel, "状态:", statusLabel);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton borrowButton = createActionButton("借阅");
        JButton reserveButton = createActionButton("预约");

        // 根据可借数量设置按钮状态
        borrowButton.setEnabled(book.getAvailableCopies() > 0);
        reserveButton.setEnabled(book.getAvailableCopies() <= 0);

        buttonPanel.add(borrowButton);
        buttonPanel.add(reserveButton);

        // 添加组件到主面板
        mainPanel.add(imagePanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(infoPanel, BorderLayout.NORTH);

        // 添加描述区域
        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setText(book.getDescription());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(BorderFactory.createTitledBorder("图书简介"));
        descriptionScroll.setPreferredSize(new Dimension(0, 150)); // 设置固定高度

        centerPanel.add(descriptionScroll, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 添加到对话框
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel labelField = new JLabel(label);
        labelField.setFont(new Font("微软雅黑", Font.BOLD, 14));
        panel.add(labelField);

        JLabel valueField = new JLabel(value);
        valueField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        panel.add(valueField);
    }

    private void addInfoRowWithComponent(JPanel panel, String label, JComponent component) {
        JLabel labelField = new JLabel(label);
        labelField.setFont(new Font("微软雅黑", Font.BOLD, 14));
        panel.add(labelField);
        panel.add(component);
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 40));
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.addActionListener(this::handleAction);
        return button;
    }

    private void updateStatusLabel(JLabel statusLabel) {
        if (book.getAvailableCopies() > 0) {
            statusLabel.setText("可借阅");
            statusLabel.setForeground(Color.GREEN);
        } else {
            statusLabel.setText("已无库存，无法借阅");
            statusLabel.setForeground(Color.RED);
        }
    }

    private void handleAction(ActionEvent e) {
        String action = e.getActionCommand();
        if ("借阅".equals(action)) {
            handleBorrowAction();
        } else if ("预约".equals(action)) {
            handleReserveAction();
        }
    }

    private void handleBorrowAction() {
        if (book.getAvailableCopies() <= 0) {
            JOptionPane.showMessageDialog(this, "该书暂无库存，无法借阅", "借阅失败",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "确定要借阅《" + book.getTitle() + "》吗？", "确认借阅", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                // 调用服务端借阅图书
                boolean success = libraryService.borrowBook(currentUser.getCid(), book.getIsbn());

                if (success) {
                    JOptionPane.showMessageDialog(this, "借阅成功！请到'我的图书馆'查看", "借阅成功",
                            JOptionPane.INFORMATION_MESSAGE);

                    // 更新图书可借数量
                    book.setAvailableCopies(book.getAvailableCopies() - 1);

                    // 刷新界面状态
                    refreshUI();

                    // 关闭对话框
                    dispose();



                } else {
                    JOptionPane.showMessageDialog(this, "借阅失败，该书已被预约或已达借阅上限或有书逾期", "借阅失败",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "借阅失败: " + ex.getMessage(), "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleReserveAction() {
        if (book.getAvailableCopies() > 0) {
            JOptionPane.showMessageDialog(this, "该书有库存，请直接借阅", "预约提示",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "确定要预约《" + book.getTitle() + "》吗？", "确认预约", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                // 调用服务端预约图书
                boolean success = libraryService.reserveBook(currentUser.getCid(), book.getIsbn());

                if (success) {
                    JOptionPane.showMessageDialog(this, "预约成功！书籍到馆后会通知您", "预约成功",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "预约失败，请稍后再试", "预约失败",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "预约失败: " + ex.getMessage(), "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshUI() {
        // 更新所有组件
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComponents = ((JPanel) comp).getComponents();
                for (Component subComp : subComponents) {
                    if (subComp instanceof JPanel) {
                        Component[] infoComponents = ((JPanel) subComp).getComponents();
                        for (Component infoComp : infoComponents) {
                            if (infoComp instanceof JLabel) {
                                JLabel label = (JLabel) infoComp;
                                if ("可借数量:".equals(label.getText())) {
                                    // 找到可借数量标签后的值标签
                                    Component nextComp = ((JPanel) subComp).getComponent(((JPanel) subComp).getComponentZOrder(label) + 1);
                                    if (nextComp instanceof JLabel) {
                                        JLabel valueLabel = (JLabel) nextComp;
                                        valueLabel.setText(String.valueOf(book.getAvailableCopies()));
                                        valueLabel.setForeground(book.getAvailableCopies() > 0 ? Color.BLACK : Color.RED);
                                    }
                                } else if ("状态:".equals(label.getText())) {
                                    // 找到状态标签后的值标签
                                    Component nextComp = ((JPanel) subComp).getComponent(((JPanel) subComp).getComponentZOrder(label) + 1);
                                    if (nextComp instanceof JLabel) {
                                        updateStatusLabel((JLabel) nextComp);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 更新按钮状态
        JPanel buttonPanel = (JPanel) getContentPane().getComponent(1);
        for (Component comp : buttonPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if ("借阅".equals(button.getText())) {
                    button.setEnabled(book.getAvailableCopies() > 0);
                } else if ("预约".equals(button.getText())) {
                    button.setEnabled(book.getAvailableCopies() <= 0);
                }
            }
        }

        // 重绘界面
        revalidate();
        repaint();
    }
}