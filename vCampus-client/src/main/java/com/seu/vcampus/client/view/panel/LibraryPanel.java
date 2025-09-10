package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.service.LibraryService;
import com.seu.vcampus.common.model.*;
import com.seu.vcampus.common.util.ImageLoader;
import com.seu.vcampus.client.view.NavigatablePanel;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class LibraryPanel extends JPanel implements NavigatablePanel {

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("图书馆管理系统");
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            frame.setSize(800, 600);
//            frame.setLocationRelativeTo(null);
//
//            LibraryPanel libraryPanel = new LibraryPanel();
//            frame.add(libraryPanel);
//            frame.setVisible(true);
//        });
//    }

    // 客户端控制器
    private LibraryService libraryService;
    // 界面组件

    private JLabel statusLabel;

    private JTabbedPane tabbedPane;
    private CardLayout cardLayout;
    private JMenuBar menuBar;
    private JPanel mainPanel;
    private User currentUser;


    private void init_User(){
        currentUser=new User();
        currentUser.setRole("AD");
        currentUser.setName("张三");
        currentUser.setCid("20210001");
        currentUser.setPassword("123456");
    }


    public LibraryPanel() {
        // 初始化客户端控制器
        libraryService = new LibraryService();
        init_User();

        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 添加主面板（初始为空）
        mainPanel.add(new JPanel(), "MAIN");

        add(mainPanel, BorderLayout.CENTER);

        showMainPanel();
        addTabChangeListener();
    }

    private void showMainPanel() {
        JPanel mainContent = new JPanel(new BorderLayout());
        tabbedPane = new JTabbedPane();

        // 添加通用面板（所有用户都有）
        tabbedPane.addTab("图书检索", new BookSearchPanel());
        tabbedPane.addTab("我的图书馆", new MyLibraryPanel());
        //管理员面板
        if("AD".equals(currentUser.getRole()))
            tabbedPane.addTab("管理员界面", new AdminPanel());
        // 创建菜单栏
        createMenuBar();

        mainContent.add(tabbedPane, BorderLayout.CENTER);

        // 更新主面板
        mainContent.add(menuBar,BorderLayout.NORTH);
        mainPanel.add(mainContent, "MAIN");

        // 显示主面板
        cardLayout.show(mainPanel, "MAIN");
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();

        JMenu systemMenu = new JMenu("系统");
        JMenuItem logoutItem = new JMenuItem("退出登录");
        logoutItem.addActionListener(e -> {
//            currentUser = null;
////            showLoginPanel();
        });

        JMenuItem exitItem = new JMenuItem("退出系统");
        exitItem.addActionListener(e -> System.exit(0));

        systemMenu.add(logoutItem);

        systemMenu.add(exitItem);

        menuBar.add(systemMenu);
    }

    private String getUserInfo() {
        if (currentUser instanceof Admin) {
            return "管理员: " + currentUser.getName();
        } else if (currentUser instanceof Student) {
            return "学生: " + currentUser.getName();
        } else if (currentUser instanceof Teacher) {
            return "教师: " + currentUser.getName();
        }
        return "用户: " + currentUser.getName();
    }


    @Override
    public void refreshPanel(User user) {

    }

    @Override
    public String getPanelName() {
        return "LIBRARY";
    }


    // 内部类：管理员面板
    private class AdminPanel extends JPanel implements RefreshablePanel {
        private JTable bookTable;
        private DefaultTableModel bookTableModel;
        private List<Book> books;
        private JButton addButton;
        private JButton editButton;
        private JButton deleteButton;
        private JButton refreshButton;

        public AdminPanel() {
            setLayout(new BorderLayout());
            initComponents();
            loadBooksFromDatabase();
        }

        private void initComponents() {
            // 创建工具栏
            JToolBar toolBar = new JToolBar();
            toolBar.setFloatable(false);

            addButton = new JButton("添加图书");
            editButton = new JButton("修改图书");
            deleteButton = new JButton("删除图书");
            refreshButton = new JButton("刷新");

            // 添加按钮监听器
            addButton.addActionListener(e -> addBook());
            editButton.addActionListener(e -> editBook()); // 新增修改按钮监听
            deleteButton.addActionListener(e -> deleteBook());
            refreshButton.addActionListener(e -> refresh());

            toolBar.add(addButton);
            toolBar.add(editButton);
            toolBar.add(deleteButton);
            toolBar.addSeparator();
            toolBar.add(refreshButton);

            // 创建图书表格
            String[] columns = {"ISBN", "书名", "作者", "出版社", "年份", "总数量", "可借数量", "位置"};
            bookTableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // 表格不可编辑
                }
            };

            bookTable = new JTable(bookTableModel);
            bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            bookTable.getTableHeader().setReorderingAllowed(false);
            bookTable.setRowHeight(30);

            // 设置列宽
            bookTable.getColumnModel().getColumn(0).setPreferredWidth(120); // ISBN
            bookTable.getColumnModel().getColumn(1).setPreferredWidth(200); // 书名
            bookTable.getColumnModel().getColumn(2).setPreferredWidth(100); // 作者
            bookTable.getColumnModel().getColumn(3).setPreferredWidth(150); // 出版社
            bookTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // 年份
            bookTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // 总数量
            bookTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // 可借数量
            bookTable.getColumnModel().getColumn(7).setPreferredWidth(100); // 位置

            JScrollPane scrollPane = new JScrollPane(bookTable);

            // 添加到面板
            add(toolBar, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
        }

        private void loadBooksFromDatabase() {
            // 通过LibraryController获取图书数据
            books = libraryService.getAllBooks();
            updateBookTable();
        }

        private void updateBookTable() {
            bookTableModel.setRowCount(0); // 清空表格

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

        private void addBook() {
            AddBookDialog dialog = new AddBookDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    book -> {
                        // 检查ISBN是否已存在
                        Book existingBook = libraryService.getBookByISBN(book.getIsbn());
                        if (existingBook != null) {
                            JOptionPane.showMessageDialog(this, "ISBN已存在，无法添加", "错误",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // 添加图书到数据库
                        boolean success = libraryService.addBook(book);
                        if (success) {
                            JOptionPane.showMessageDialog(this, "添加图书成功！", "成功",
                                    JOptionPane.INFORMATION_MESSAGE);
                            refresh(); // 刷新表格
                        } else {
                            JOptionPane.showMessageDialog(this, "添加图书失败", "错误",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
            );
            dialog.setVisible(true);
        }

        // 修改图书方法
        private void editBook() {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "请选择要修改的图书", "提示",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 获取选中的图书ISBN
            String isbn = (String) bookTable.getValueAt(selectedRow, 0);

            // 从服务器获取完整的图书信息
            Book bookToEdit = libraryService.getBookByISBN(isbn);
            if (bookToEdit == null) {
                JOptionPane.showMessageDialog(this, "无法获取图书信息", "错误",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 创建修改图书对话框
            EditBookDialog dialog = new EditBookDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    bookToEdit,
                    updatedBook -> {
                        // 更新图书到数据库
                        boolean success = libraryService.updateBook(updatedBook);
                        if (success) {
                            JOptionPane.showMessageDialog(this, "修改图书成功！", "成功",
                                    JOptionPane.INFORMATION_MESSAGE);
                            refresh(); // 刷新表格
                        } else {
                            JOptionPane.showMessageDialog(this, "修改图书失败", "错误",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
            );
            dialog.setVisible(true);
        }

        private void deleteBook() {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "请选择要删除的图书", "提示",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String isbn = (String) bookTable.getValueAt(selectedRow, 0);
            String title = (String) bookTable.getValueAt(selectedRow, 1);

            int choice = JOptionPane.showConfirmDialog(this,
                    "确定要删除《" + title + "》吗？", "确认删除", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                // 从数据库删除图书
                boolean success = libraryService.deleteBook(isbn);
                if (success) {
                    JOptionPane.showMessageDialog(this, "删除图书成功！", "成功",
                            JOptionPane.INFORMATION_MESSAGE);
                    refresh(); // 刷新表格
                } else {
                    JOptionPane.showMessageDialog(this, "删除图书失败", "错误",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        @Override
        public void refresh() {
            loadBooksFromDatabase();
        }


    }

    //修改图书对话框
    private class EditBookDialog extends JDialog {
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
            setMinimumSize(new Dimension(500, 400));

            // 创建表单面板
            JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));

            // ISBN（不可编辑）
            formPanel.add(new JLabel("ISBN:"));
            isbnField = new JTextField(book.getIsbn());
            isbnField.setEditable(false);
            formPanel.add(isbnField);

            // 书名
            formPanel.add(new JLabel("书名:"));
            titleField = new JTextField(book.getTitle());
            formPanel.add(titleField);

            // 作者
            formPanel.add(new JLabel("作者:"));
            authorField = new JTextField(book.getAuthor());
            formPanel.add(authorField);

            // 出版社
            formPanel.add(new JLabel("出版社:"));
            publisherField = new JTextField(book.getPublisher());
            formPanel.add(publisherField);

            // 出版年份
            formPanel.add(new JLabel("出版年份:"));
            publishYearSpinner = new JSpinner(new SpinnerNumberModel(
                    book.getPublishYear(), 0, Calendar.getInstance().get(Calendar.YEAR), 1));
            formPanel.add(publishYearSpinner);

            // 总数量
            formPanel.add(new JLabel("总数量:"));
            totalCopiesSpinner = new JSpinner(new SpinnerNumberModel(
                    book.getTotalCopies(), 1, 1000, 1));
            formPanel.add(totalCopiesSpinner);

            // 可借数量
            formPanel.add(new JLabel("可借数量:"));
            availableCopiesSpinner = new JSpinner(new SpinnerNumberModel(
                    book.getAvailableCopies(), 0, book.getTotalCopies(), 1));
            formPanel.add(availableCopiesSpinner);

            // 位置
            formPanel.add(new JLabel("位置:"));
            locationField = new JTextField(book.getLocation());
            formPanel.add(locationField);

            // 图片路径
            formPanel.add(new JLabel("图片路径:"));
            imagePathField = new JTextField(book.getImagePath());
            formPanel.add(imagePathField);

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

    private class AddBookDialog extends JDialog {
        private Consumer<Book> onAddCallback;

        private JTextField isbnField;
        private JTextField titleField;
        private JTextField authorField;
        private JTextField publisherField;
        private JTextField yearField;
        private JTextField totalCopiesField;
        private JTextField locationField;
        private JTextField imagePathField;

        public AddBookDialog(Frame owner, Consumer<Book> onAddCallback) {
            super(owner, "添加新书", true);
            this.onAddCallback = onAddCallback;
            setSize(500, 400);
            setLocationRelativeTo(owner);
            initComponents();
        }

        private void initComponents() {
            setLayout(new GridLayout(9, 2, 10, 10));

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
            add(new JLabel("ISBN:"));
            add(isbnField);
            add(new JLabel("书名:"));
            add(titleField);
            add(new JLabel("作者:"));
            add(authorField);
            add(new JLabel("出版社:"));
            add(publisherField);
            add(new JLabel("出版年份:"));
            add(yearField);
            add(new JLabel("总数量:"));
            add(totalCopiesField);
            add(new JLabel("位置:"));
            add(locationField);
            add(new JLabel("图片路径:"));
            add(imagePathField);

            // 添加按钮
            JButton addButton = new JButton("添加");
            addButton.addActionListener(e -> addBook());

            JButton cancelButton = new JButton("取消");
            cancelButton.addActionListener(e -> dispose());

            add(addButton);
            add(cancelButton);
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
    //搜索界面
    private class BookSearchPanel extends JPanel implements RefreshablePanel{
        private JTextField searchField;
        private JComboBox<String> searchTypeCombo;
        private JButton searchButton;
        private JButton resetButton;


        private List<Book> books; // 添加成员变量
        private JTable bookTable;
        private DefaultTableModel bookTableModel;



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
                    LibraryPanel.this.libraryService,  // 使用外部类引用
                    LibraryPanel.this.currentUser         // 使用外部类引用
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

    //我的图书馆
    private class MyLibraryPanel extends  JPanel implements RefreshablePanel{
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


        private List<BorrowRecord> borrowRecords;


        public MyLibraryPanel() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 10, 10));
            initComponents();

//            loadBorrowRecords(currentUser.getCid());
//            loadReservations();
//            loadUserData();
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
            returnButton.addActionListener(e -> returnBook());

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
            userNameLabel.setText("姓名: " + currentUser.getName());
            userIdLabel.setText("学号: " + currentUser.getCid());

            borrowCountLabel.setText("借阅数量: "+borrowRecords.getLast().getRecordId());
            reservationCountLabel.setText("预约数量: 2");
        }


        public void loadBorrowRecords(String UserID) {
            this.borrowRecords = libraryService.getBorrowRecordsByUserId(UserID); // 保存图书列表
            borrowTableModel.setRowCount(0);

            for (BorrowRecord borrows : borrowRecords) {
                Object[] rowData = {
                        borrows.getBookIsbn(),
                        borrows.getBookTitle(),
                        borrows.getBorrowDate(),
                        borrows.getDueDate(),
                        borrows.getStatus()
                };
                borrowTableModel.addRow(rowData);
            }
        }

        public void loadReservations() {
            // 模拟预约记录数据
            reservationTableModel.setRowCount(0);

            // 添加模拟数据
            reservationTableModel.addRow(new Object[]{"9787121382061", "算法导论", "2023-05-18", "等待中"});
            reservationTableModel.addRow(new Object[]{"9787115480655", "数据库系统概念", "2023-05-20", "可借阅"});

            reservationCountLabel.setText("预约数量: " + reservationTableModel.getRowCount());
        }

        // 归还图书方法
        private void returnBook() {
            int selectedRow = borrowTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "请选择要归还的图书", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 获取选中的借阅记录
            BorrowRecord record = borrowRecords.get(selectedRow);

            // 检查是否已归还
            if ("RETURNED".equals(record.getStatus())) {
                JOptionPane.showMessageDialog(this, "该书已归还，无需再次操作", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int choice = JOptionPane.showConfirmDialog(this,
                    "确定要归还《" + record.getBookTitle() + "》吗？", "确认归还", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                try {
                    // 调用服务端归还图书
                    boolean success = libraryService.returnBook(record.getRecordId());

                    if (success) {
                        JOptionPane.showMessageDialog(this, "归还成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

                        // 更新本地记录状态
                        record.setStatus("RETURNED");
                        record.setReturnDate(new Date());

                        // 更新表格显示
                        borrowTableModel.setValueAt("已归还", selectedRow, 4);

                        // 刷新界面
                        refresh();
                    } else {
                        JOptionPane.showMessageDialog(this, "归还失败，请稍后再试", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "归还失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
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


        @Override
        public void refresh() {
            loadUserData();
            loadBorrowRecords(currentUser.getCid());
            loadReservations();
        }
    }

    //书籍详情
    public class BookDetailDialog extends JDialog {
        private Book book;
        private LibraryService libraryService;
        private User currentUser;

        public BookDetailDialog(Frame owner, Book book, LibraryService libraryService, User currentUser) {
            super(owner, "图书详情", true);
            this.book = book;
            this.libraryService = libraryService;
            this.currentUser = currentUser;
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
            descriptionArea.setText("《" + book.getTitle() + "》是一本经典的计算机科学教材，详细介绍了" + book.getTitle() + "的核心概念和实践应用。");
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
//                handleReserveAction();
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
                        JOptionPane.showMessageDialog(this, "借阅失败，请稍后再试", "借阅失败",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "借阅失败: " + ex.getMessage(), "错误",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }

//        private void handleReserveAction() {
//            if (book.getAvailableCopies() > 0) {
//                JOptionPane.showMessageDialog(this, "该书有库存，请直接借阅", "预约提示",
//                        JOptionPane.INFORMATION_MESSAGE);
//                return;
//            }
//
//            int choice = JOptionPane.showConfirmDialog(this,
//                    "确定要预约《" + book.getTitle() + "》吗？", "确认预约", JOptionPane.YES_NO_OPTION);
//
//            if (choice == JOptionPane.YES_OPTION) {
//                try {
//                    // 调用服务端预约图书
//                    boolean success = libraryController.reserveBook(currentUser.getCid(), book.getIsbn());
//
//                    if (success) {
//                        JOptionPane.showMessageDialog(this, "预约成功！书籍到馆后会通知您", "预约成功",
//                                JOptionPane.INFORMATION_MESSAGE);
//                        dispose();
//                    } else {
//                        JOptionPane.showMessageDialog(this, "预约失败，请稍后再试", "预约失败",
//                                JOptionPane.ERROR_MESSAGE);
//                    }
//                } catch (Exception ex) {
//                    JOptionPane.showMessageDialog(this, "预约失败: " + ex.getMessage(), "错误",
//                            JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        }

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

    //切换页面刷新
    private void addTabChangeListener() {
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex >= 0) {
                Component selectedComponent = tabbedPane.getComponentAt(selectedIndex);
                if (selectedComponent instanceof RefreshablePanel) {
                    ((RefreshablePanel) selectedComponent).refresh();
                }
            }
        });
    }

    interface RefreshablePanel {
        void refresh();
    }


}