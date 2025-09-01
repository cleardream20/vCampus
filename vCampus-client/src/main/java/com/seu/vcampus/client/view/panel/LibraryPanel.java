package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.controller.LibraryController;
import com.seu.vcampus.common.model.Book;
import com.seu.vcampus.common.util.ImageLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        private List<Book> books; // 添加成员变量




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
            bookTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // 总数量
            bookTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // 可借数量
            bookTable.getColumnModel().getColumn(7).setPreferredWidth(100); // 位置

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

            // 操作按钮面板
//            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
//            JButton borrowButton = createOperationButton("借阅");
//            JButton returnButton = createOperationButton("归还");
//            JButton reserveButton = createOperationButton("预约");
//
//            buttonPanel.add(borrowButton);
//            buttonPanel.add(returnButton);
//            buttonPanel.add(reserveButton);

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
                    (Frame) SwingUtilities.getWindowAncestor(this), book);
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
                List<Book> books = libraryController.getAllBooks();
                updateBookTable(books);
                statusLabel.setText("成功加载 " + books.size() + " 本图书");
            } catch (Exception e) {
                statusLabel.setText("加载失败: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "加载图书失败: " + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }





    }
    private class MyLibraryPanel extends  JPanel{
        private JTable borrowTable;
        private DefaultTableModel borrowTableModel;
        private JTable reservationTable;
        private DefaultTableModel reservationTableModel;
        private JPanel accountPanel;
        private JLabel userNameLabel;
        private JLabel userIdLabel;
        private JLabel borrowCountLabel;
        private JLabel reservationCountLabel;
        private JButton renewButton;
        private JButton returnButton;
        private JButton cancelReservationButton;
        public MyLibraryPanel() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 10, 10));
            initComponents();
            loadUserData();
            loadBorrowRecords();
            loadReservations();
        }
        private void initComponents() {
            // 创建账户信息面板
            accountPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            accountPanel.setBorder(new TitledBorder("账户信息"));

            userNameLabel = new JLabel("姓名: ");
            userIdLabel = new JLabel("学号: ");
            borrowCountLabel = new JLabel("借阅数量: ");
            reservationCountLabel = new JLabel("预约数量: ");

            accountPanel.add(userNameLabel);
            accountPanel.add(userIdLabel);
            accountPanel.add(borrowCountLabel);
            accountPanel.add(reservationCountLabel);

            // 创建借阅记录面板
            JPanel borrowPanel = new JPanel(new BorderLayout());
            borrowPanel.setBorder(new TitledBorder("我的借阅"));

            String[] borrowColumns = {"ISBN", "书名", "借阅日期", "应还日期", "状态"};
            borrowTableModel = new DefaultTableModel(borrowColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            borrowTable = new JTable(borrowTableModel);
            borrowTable.setRowHeight(30);
            borrowTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    updateButtonStates();
                }
            });

            JScrollPane borrowScrollPane = new JScrollPane(borrowTable);
            borrowPanel.add(borrowScrollPane, BorderLayout.CENTER);

            // 创建预约信息面板
            JPanel reservationPanel = new JPanel(new BorderLayout());
            reservationPanel.setBorder(new TitledBorder("我的预约"));

            String[] reservationColumns = {"ISBN", "书名", "预约日期", "状态"};
            reservationTableModel = new DefaultTableModel(reservationColumns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            reservationTable = new JTable(reservationTableModel);
            reservationTable.setRowHeight(30);
            reservationTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    updateButtonStates();
                }
            });

            JScrollPane reservationScrollPane = new JScrollPane(reservationTable);
            reservationPanel.add(reservationScrollPane, BorderLayout.CENTER);

            // 创建操作按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            renewButton = new JButton("续借");
            renewButton.setEnabled(false);
//            renewButton.addActionListener(e -> renewBook());

            returnButton = new JButton("归还");
            returnButton.setEnabled(false);
//            returnButton.addActionListener(e -> returnBook());

            cancelReservationButton = new JButton("取消预约");
            cancelReservationButton.setEnabled(false);
//            cancelReservationButton.addActionListener(e -> cancelReservation());

            buttonPanel.add(renewButton);
            buttonPanel.add(returnButton);
            buttonPanel.add(cancelReservationButton);

            // 使用分割面板布局
            JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splitPane.setTopComponent(borrowPanel);
            splitPane.setBottomComponent(reservationPanel);
            splitPane.setDividerLocation(200);
            splitPane.setResizeWeight(0.5);

            // 添加到主面板
            add(accountPanel, BorderLayout.NORTH);
            add(splitPane, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void loadUserData() {
            // 模拟用户数据
            userNameLabel.setText("姓名: " + "张三");
            userIdLabel.setText("学号: " + "2132000001");
            borrowCountLabel.setText("借阅数量: 3");
            reservationCountLabel.setText("预约数量: 2");
        }

        public void loadBorrowRecords() {
            // 模拟借阅记录数据
            borrowTableModel.setRowCount(0);

            // 添加模拟数据
            borrowTableModel.addRow(new Object[]{"9787111636665", "Java核心技术", "2023-05-10", "2023-06-10", "在借"});
            borrowTableModel.addRow(new Object[]{"9787302518383", "Python编程", "2023-05-15", "2023-06-15", "在借"});
            borrowTableModel.addRow(new Object[]{"9787115537977", "深入理解计算机系统", "2023-04-20", "2023-05-20", "逾期"});
            borrowCountLabel.setText("借阅数量: " + borrowTableModel.getRowCount());
        }

        public void loadReservations() {
            // 模拟预约记录数据
            reservationTableModel.setRowCount(0);

            // 添加模拟数据
            reservationTableModel.addRow(new Object[]{"9787121382061", "算法导论", "2023-05-18", "等待中"});
            reservationTableModel.addRow(new Object[]{"9787115480655", "数据库系统概念", "2023-05-20", "可借阅"});

            reservationCountLabel.setText("预约数量: " + reservationTableModel.getRowCount());
        }

        private void updateButtonStates() {
            // 更新借阅操作按钮状态
            int borrowSelectedRow = borrowTable.getSelectedRow();
            renewButton.setEnabled(borrowSelectedRow >= 0);
            returnButton.setEnabled(borrowSelectedRow >= 0);

            // 更新预约操作按钮状态
            int reservationSelectedRow = reservationTable.getSelectedRow();
            cancelReservationButton.setEnabled(reservationSelectedRow >= 0);
        }
    }


    public class BookDetailDialog extends JDialog {
        private Book book;

        public BookDetailDialog(Frame owner, Book book) {
            super(owner, "图书详情", true);
            this.book = book;
            setSize(600, 500);
            setLocationRelativeTo(owner);
            initComponents();
        }

        private void initComponents() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            // 创建主面板
            JPanel mainPanel = new JPanel(new BorderLayout(20, 20));

            // 图片面板
            JPanel imagePanel = new JPanel(new BorderLayout());
            imagePanel.setBorder(BorderFactory.createTitledBorder("封面"));

            // 加载图片
            ImageIcon bookImage = ImageLoader.loadImage(book.getImagePath());
            if (bookImage == null) {
                bookImage = new ImageIcon(getClass().getResource("D:\\idea_project\\vCampus\\Images\\default_book.jpg"));
            }

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
            addInfoRow(infoPanel, "可借数量:", String.valueOf(book.getAvailableCopies()));
            addInfoRow(infoPanel, "位置:", book.getLocation());

            // 按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            JButton borrowButton = createActionButton("借阅");
            JButton reserveButton = createActionButton("预约");

            buttonPanel.add(borrowButton);
            buttonPanel.add(reserveButton);

            // 添加组件到主面板
            mainPanel.add(imagePanel, BorderLayout.WEST);
            mainPanel.add(infoPanel, BorderLayout.CENTER);

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

        private JButton createActionButton(String text) {
            JButton button = new JButton(text);
            button.setPreferredSize(new Dimension(120, 40));
            button.setFont(new Font("微软雅黑", Font.BOLD, 16));
            button.addActionListener(this::handleAction);
            return button;
        }

        private void handleAction(ActionEvent e) {
            String action = e.getActionCommand();
            if ("借阅".equals(action)) {
                if (book.getAvailableCopies() <= 0) {
                    JOptionPane.showMessageDialog(this, "该书暂无库存，无法借阅", "借阅失败", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int choice = JOptionPane.showConfirmDialog(this,
                        "确定要借阅《" + book.getTitle() + "》吗？", "确认借阅", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(this, "借阅成功！请到'我的图书馆'查看", "借阅成功",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            } else if ("预约".equals(action)) {
                if (book.getAvailableCopies() > 0) {
                    JOptionPane.showMessageDialog(this, "该书有库存，请直接借阅", "预约提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int choice = JOptionPane.showConfirmDialog(this,
                        "确定要预约《" + book.getTitle() + "》吗？", "确认预约", JOptionPane.YES_NO_OPTION);

                if (choice == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(this, "预约成功！书籍到馆后会通知您", "预约成功",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            }
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



}