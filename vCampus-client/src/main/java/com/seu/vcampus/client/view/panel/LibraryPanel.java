package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.controller.LibraryController;
import com.seu.vcampus.common.model.Book;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class LibraryPanel extends JPanel {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("图书馆管理系统");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            LibraryPanel libraryPanel = new LibraryPanel();
            frame.add(libraryPanel);
            frame.setVisible(true);
        });
    }

    // 客户端控制器
    private LibraryController libraryController;

    // 界面组件

    private JLabel statusLabel;
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private JTabbedPane tabbedPane;
    private BookSearchPanel bookSearchPanel;
    private MyLibraryPanel myLibraryPanel;


    public LibraryPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 初始化客户端控制器
        libraryController = new LibraryController();

        initComponents();
    }

    private void initComponents() {
        // 创建选项卡面板
        tabbedPane = new JTabbedPane();

        // 创建图书检索面板
        bookSearchPanel = new BookSearchPanel();

        // 创建我的图书馆面板
        myLibraryPanel = new MyLibraryPanel();

        // 添加选项卡
        tabbedPane.addTab("图书检索", bookSearchPanel);
        tabbedPane.addTab("我的图书馆", myLibraryPanel);

        // 添加到主面板
        add(tabbedPane, BorderLayout.CENTER);
    }
    private class BookSearchPanel extends JPanel{
        private JTextField searchField;
        private JComboBox<String> searchTypeCombo;
        private JButton searchButton;
        private JButton resetButton;




        public BookSearchPanel() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 10, 10));
            initComponents();
            loadBooksFromServer();
        }

        private void initComponents() {
            // 创建搜索面板
            JPanel searchPanel = new JPanel();
            searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
            searchPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

            // 搜索类型选择
            searchTypeCombo = new JComboBox<>(new String[]{"书名", "作者", "ISBN", "位置"});

            // 搜索字段
            searchField = new JTextField(20);

            // 搜索按钮
            searchButton = new JButton("搜索");
            searchButton.setPreferredSize(new Dimension(80, 30));
            searchButton.addActionListener(this::performSearch);

            // 重置按钮
            resetButton = new JButton("重置");
            resetButton.setPreferredSize(new Dimension(80, 30));
            resetButton.addActionListener(e -> resetSearch());

            // 添加到搜索面板
            searchPanel.add(new JLabel("搜索类型:"));
            searchPanel.add(searchTypeCombo);
            searchPanel.add(new JLabel("关键词:"));
            searchPanel.add(searchField);
            searchPanel.add(searchButton);
            searchPanel.add(resetButton);

            // 图书表格
            String[] columns = {"ISBN", "书名", "作者", "出版社", "年份", "库存数量","可借数量", "位置"};
            bookTableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // 所有单元格不可编辑
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 5) return Integer.class; // 可借数量是整数
                    return String.class;
                }
            };

            bookTable = new JTable(bookTableModel);
            bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            bookTable.getTableHeader().setReorderingAllowed(false);
            bookTable.setRowHeight(30); // 设置行高

            // 设置可借数量的特殊渲染
            bookTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    if (value instanceof Integer) {
                        int availableCopies = (Integer) value;
                        if (availableCopies == 0) {
                            c.setForeground(Color.RED);
                        } else {
                            c.setForeground(Color.BLACK);
                        }
                    }
                    return c;
                }
            });

            // 设置列宽
            bookTable.getColumnModel().getColumn(0).setPreferredWidth(120); // ISBN
            bookTable.getColumnModel().getColumn(1).setPreferredWidth(200); // 书名
            bookTable.getColumnModel().getColumn(2).setPreferredWidth(100); // 作者
            bookTable.getColumnModel().getColumn(3).setPreferredWidth(150); // 出版社
            bookTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // 年份
            bookTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // 可借数量
            bookTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // 可借数量
            bookTable.getColumnModel().getColumn(7).setPreferredWidth(100); // 位置

            JScrollPane scrollPane = new JScrollPane(bookTable);

            // 操作按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            JButton borrowButton = createOperationButton("借阅");
            JButton returnButton = createOperationButton("归还");
            JButton reserveButton = createOperationButton("预约");

            buttonPanel.add(borrowButton);
            buttonPanel.add(returnButton);
            buttonPanel.add(reserveButton);

            // 状态栏
            statusLabel = new JLabel("就绪");
            statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));

            // 添加到主面板
            add(searchPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
            add(statusLabel, BorderLayout.SOUTH);
        }
        /**
         * 执行搜索操作
         */
        private void performSearch(ActionEvent e) {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                resetSearch();
                return;
            }

            statusLabel.setText("正在搜索: " + keyword + "...");

            try {
                List<Book> books = libraryController.searchBooks(keyword);
                updateBookTable(books);

                if (books.isEmpty()) {
                    statusLabel.setText("没有找到匹配的图书");
                    JOptionPane.showMessageDialog(this, "没有找到匹配的图书",
                            "搜索结果", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    statusLabel.setText("找到 " + books.size() + " 本匹配图书");
                }
            } catch (Exception ex) {
                statusLabel.setText("搜索失败: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "搜索失败: " + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        /**
         * 重置搜索
         */
        private void resetSearch() {
            searchField.setText("");
            loadBooksFromServer();
            statusLabel.setText("重置成功，显示所有图书");
        }



    }
    private class MyLibraryPanel extends  JPanel{
        public MyLibraryPanel() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 10, 10));
            initComponents();
        }
        private void initComponents()
        {

        }
    }

    private JButton createOperationButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(80, 35));
        button.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    text + "功能尚未实现",
                    "功能未完成",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        return button;
    }

    /**
     * 从服务端加载图书数据
     */
    private void loadBooksFromServer() {
        statusLabel.setText("正在加载图书数据...");

        try {
            List<Book> books = libraryController.getAllBooks();
            updateBookTable(books);
            statusLabel.setText("成功加载 " + books.size() + " 本图书");
        } catch (Exception e) {
            statusLabel.setText("加载失败: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "加载图书失败: " + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 更新图书表格
     */
    private void updateBookTable(List<Book> books) {
        // 清空表格
        bookTableModel.setRowCount(0);

        // 添加新数据
        for (Book book : books) {
            Object[] rowData = {
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPublisher(),
                    book.getPublishYear(),
                    book.getTotalCopies(),
                    book.getAvailableCopies(),
                    book.getLocation()
            };
            bookTableModel.addRow(rowData);
        }
    }
}