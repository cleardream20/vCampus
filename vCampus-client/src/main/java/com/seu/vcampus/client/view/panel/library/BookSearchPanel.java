package com.seu.vcampus.client.view.panel.library;

import com.seu.vcampus.client.service.LibraryService;
import com.seu.vcampus.client.view.panel.LibraryPanel;
import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.client.view.panel.library.dialog.*;
import com.seu.vcampus.common.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class BookSearchPanel extends JPanel implements LibraryMainPanel.Refreshable {
    private JTextField searchField;
    private JLabel statusLabel;
    private JComboBox<String> searchTypeCombo;
    private JButton searchButton;
    private JButton resetButton;
    private User currentUser;

    private List<Book> books; // 添加成员变量
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private final LibraryService libraryService = new LibraryService();



    public BookSearchPanel(User currentUser) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        this.currentUser= currentUser;
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
        bookTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // 总数量
        bookTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // 可借数量
        bookTable.getColumnModel().getColumn(7).setPreferredWidth(100); // 位置
        //鼠标双击事件

        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = bookTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        // 获取图书对象
                        Book book = books.get(selectedRow);
                        // 显示详情对话框
                        showBookDetail(book);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(bookTable);



        // 状态栏
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));

        // 添加到主面板
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
//            add(buttonPanel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void showBookDetail(Book book) {
        BookDetailDialog detailDialog = new BookDetailDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                book,
                libraryService,  // 使用外部类引用
                currentUser     // 使用外部类引用
        );
        detailDialog.setVisible(true);
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
            List<Book> books = libraryService.searchBooks(keyword);
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

    /**
     * 更新图书表格
     */
    private void updateBookTable(List<Book> books) {
        this.books = books; // 保存图书列表
        bookTableModel.setRowCount(0);

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

    /**
     * 从服务端加载图书数据
     */
    private void loadBooksFromServer() {
        statusLabel.setText("正在加载图书数据...");

        try {
            List<Book> books = libraryService.getAllBooks();
            updateBookTable(books);
            statusLabel.setText("成功加载 " + books.size() + " 本图书");
        } catch (Exception e) {
            statusLabel.setText("加载失败: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "加载图书失败: " + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void refresh() {
        loadBooksFromServer();
    }

}
