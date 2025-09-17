package com.seu.vcampus.client.view.panel.library;

import com.seu.vcampus.client.service.LibraryService;
import com.seu.vcampus.client.view.panel.library.dialog.AddBookDialog;
import com.seu.vcampus.client.view.panel.library.dialog.EditBookDialog;
import com.seu.vcampus.common.model.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel implements LibraryMainPanel.Refreshable {
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    private List<Book> books;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private final LibraryService libraryService = new LibraryService();

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

            }
        }                JOptionPane.showMessageDialog(this, "删除图书失败", "错误",
                        JOptionPane.ERROR_MESSAGE);
            }

    @Override
    public void refresh() {
        loadBooksFromDatabase();
    }


}