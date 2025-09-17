package com.seu.vcampus.client.view.panel.library;
import com.seu.vcampus.client.service.LibraryService;
import com.seu.vcampus.common.model.BorrowRecord;
import com.seu.vcampus.common.model.Reservation;
import com.seu.vcampus.common.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class MyLibraryPanel extends JPanel implements LibraryMainPanel.Refreshable {
    private JTable borrowTable;
    private DefaultTableModel borrowTableModel;
    private User currentUser;
    private static final int MAX_RENEWAL_COUNT = 3;

    private JTable reservationTable;
    private DefaultTableModel reservationTableModel;
    private final LibraryService libraryService = new LibraryService();


    private JPanel accountPanel;
    private JLabel userNameLabel;
    private JLabel userIdLabel;
    private JLabel borrowCountLabel;
    private JLabel reservationCountLabel;
    private JButton renewButton;
    private JButton returnButton;
    private JButton cancelReservationButton;
    private JButton refreshButton; // 新增刷新按钮
    private JButton borrowReservedButton;


    private List<BorrowRecord> borrowRecords;
    private List<Reservation> reservations;

    public MyLibraryPanel(User currentUser) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        initComponents();
        this.currentUser= currentUser;
        loadBorrowRecords(currentUser.getCid());
        loadReservations(currentUser.getCid());
        loadUserData();

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

        // 创建刷新按钮面板
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refresh());
        refreshPanel.add(refreshButton);
        // 创建账户信息容器面板
        JPanel accountContainer = new JPanel(new BorderLayout());
        accountContainer.add(accountPanel, BorderLayout.CENTER);
        accountContainer.add(refreshPanel, BorderLayout.EAST); // 将刷新按钮放在右侧

        // 创建借阅记录面板
        JPanel borrowPanel = new JPanel(new BorderLayout());
        borrowPanel.setBorder(new TitledBorder("我的借阅"));

        String[] borrowColumns = {"ISBN", "书名", "借阅日期", "应还日期", "状态","逾期费用","续借次数"};
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

        // 自定义表格渲染器 - 为状态列设置特殊颜色
        // 为状态列（索引4）设置自定义渲染器
        borrowTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // 确保只处理状态列
                if (column == 4 && value instanceof String) {
                    String status = ((String) value).toUpperCase();

                    // 设置状态文本居中显示
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);

                    // 根据状态设置不同颜色
                    switch (status) {
                        case "BORROWED":
                            c.setForeground(new Color(0, 128, 0));  // 深绿色
                            c.setBackground(new Color(220, 255, 220)); // 浅绿色背景
                            break;
                        case "OVERDUE":
                            c.setForeground(Color.RED);
                            c.setBackground(new Color(255, 230, 230)); // 浅红色背景
                            break;
                        default:
                            // 默认样式
                            c.setForeground(Color.BLACK);
                            c.setBackground(Color.WHITE);
                    }

                    // 设置粗体显示
                    c.setFont(c.getFont().deriveFont(Font.BOLD));

                    // 设置单元格边框
                    ((JComponent) c).setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                            BorderFactory.createEmptyBorder(0, 5, 0, 5)
                    ));
                }

                return c;
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

        reservationTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // 确保只处理状态列
                if (column == 3 && value instanceof String) {
                    String status = ((String) value).toUpperCase();

                    // 设置状态文本居中显示
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);

                    // 根据状态设置不同颜色
                    switch (status) {
                        case "ACTIVE":
                            c.setForeground(new Color(0, 128, 0));  // 深绿色
                            c.setBackground(new Color(220, 255, 220)); // 浅绿色背景
                            break;
                        case "PENDING":
                            c.setForeground(Color.RED);
                            c.setBackground(new Color(255, 230, 230)); // 浅红色背景
                            break;
                        default:
                            // 默认样式
                            c.setForeground(Color.BLACK);
                            c.setBackground(Color.WHITE);
                    }

                    // 设置粗体显示
                    c.setFont(c.getFont().deriveFont(Font.BOLD));

                    // 设置单元格边框
                    ((JComponent) c).setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                            BorderFactory.createEmptyBorder(0, 5, 0, 5)
                    ));
                }

                return c;
            }
        });

        JScrollPane reservationScrollPane = new JScrollPane(reservationTable);
        reservationPanel.add(reservationScrollPane, BorderLayout.CENTER);

        // 创建操作按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        renewButton = new JButton("续借");
        renewButton.setEnabled(false);
        renewButton.addActionListener(e -> renewBook());

        returnButton = new JButton("归还");
        returnButton.setEnabled(false);
        returnButton.addActionListener(e -> returnBook());

        cancelReservationButton = new JButton("取消预约");
        cancelReservationButton.setEnabled(false);
        cancelReservationButton.addActionListener(e -> cancelReservation());

        borrowReservedButton = new JButton("借阅预约图书");
        borrowReservedButton.setEnabled(false);
        borrowReservedButton.addActionListener(e -> borrowReservedBook());
        borrowReservedButton.setToolTipText("借阅您已预约且可用的图书");

        buttonPanel.add(renewButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(cancelReservationButton);
        buttonPanel.add(borrowReservedButton);

        // 使用分割面板布局
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(borrowPanel);
        splitPane.setBottomComponent(reservationPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        // 添加到主面板
        add(accountContainer, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadUserData() {
        // 模拟用户数据
        userNameLabel.setText("姓名: " + currentUser.getName());
        userIdLabel.setText("学号: " + currentUser.getCid());

        borrowCountLabel.setText("借阅数量: "+borrowRecords.size());
        reservationCountLabel.setText("预约数量: "+reservations.size());
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
                    borrows.getStatus(),
                    borrows.getFineAmount(),
                    borrows.getRenewalCount()
            };
            borrowTableModel.addRow(rowData);
        }
    }

    public void loadReservations(String UserID) {
        this.reservations = libraryService.getReservationsByUserId(UserID);// 保存图书列表
        reservationTableModel.setRowCount(0);

        for(Reservation reservation : reservations)
        {
            Object[] rowData = {
                    reservation.getBookIsbn(),
                    reservation.getBookTitle(),
                    reservation.getReserveDate(),
                    reservation.getStatus()
            };
            reservationTableModel.addRow(rowData);
        }
    }

    // 新增：借阅预约图书方法
    private void borrowReservedBook() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请选择要借阅的预约图书", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Reservation reservation = reservations.get(selectedRow);

        // 检查预约状态
        if (!"ACTIVE".equals(reservation.getStatus())) {
            JOptionPane.showMessageDialog(this, "只有状态为'ACTIVE'的预约可以借阅", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "确定要借阅《" + reservation.getBookTitle() + "》吗？",
                "确认借阅预约图书", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                // 调用服务端借阅图书
                boolean success = libraryService.borrowBook(currentUser.getCid(), reservation.getBookIsbn());

                if (success) {
                    JOptionPane.showMessageDialog(this, "借阅成功！请到'我的借阅'查看", "借阅成功",
                            JOptionPane.INFORMATION_MESSAGE);

                    // 刷新界面
                    refresh();
                }
            } catch (Exception ex) {
                // 错误处理
            }
        }
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

    private void cancelReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请选择要取消的预约", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 获取选中的预约记录
        Reservation reservation = reservations.get(selectedRow);

        // 检查预约状态
        if ("CANCELLED".equals(reservation.getStatus())) {
            JOptionPane.showMessageDialog(this, "该预约已取消，无需再次操作", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if ("EXPIRED".equals(reservation.getStatus())) {
            JOptionPane.showMessageDialog(this, "该预约已过期，无法取消", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "确定要取消《" + reservation.getBookTitle() + "》的预约吗？",
                "确认取消预约", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                // 调用服务端取消预约
                boolean success = libraryService.cancelReservation(reservation.getReserveId());

                if (success) {
                    JOptionPane.showMessageDialog(this, "预约取消成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

                    // 更新本地记录状态
                    reservation.setStatus("CANCELLED");

                    // 刷新界面
                    refresh();
                } else {
                    JOptionPane.showMessageDialog(this, "取消预约失败，请稍后再试", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "取消预约失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void renewBook() {
        int selectedRow = borrowTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请选择要续借的图书", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 获取选中的借阅记录
        BorrowRecord record = borrowRecords.get(selectedRow);

        // 检查是否可以续借
        if (!canRenewBook(record)) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "确定要续借《" + record.getBookTitle() + "》吗？", "确认续借", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                // 调用服务端续借图书
                boolean success = libraryService.renewBook(record.getRecordId());

                if (success) {
                    // 刷新借阅记录
                    loadBorrowRecords(currentUser.getCid());

                    // 查找更新后的记录
                    BorrowRecord updatedRecord = findUpdatedRecord(record.getRecordId());

                    if (updatedRecord != null) {
                        JOptionPane.showMessageDialog(this,
                                "续借成功！新的应还日期: " + updatedRecord.getDueDate(),
                                "成功", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "续借成功！",
                                "成功", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "续借失败，该书已被预约",
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "续借失败: " + ex.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private BorrowRecord findUpdatedRecord(long recordId) {
        for (BorrowRecord record : borrowRecords) {
            if (record.getRecordId() == recordId) {
                return record;
            }
        }
        return null;
    }

    private boolean canRenewBook(BorrowRecord record) {
        // 检查状态
        if (!"BORROWED".equals(record.getStatus()) && !"OVERDUE".equals(record.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "只有借阅中或逾期的图书可以续借",
                    "无法续借", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 检查续借次数
        if (record.getRenewalCount() >= MAX_RENEWAL_COUNT) {
            JOptionPane.showMessageDialog(this,
                    "该图书已达到最大续借次数(" + MAX_RENEWAL_COUNT + "次)",
                    "无法续借", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 检查逾期状态
        if ("OVERDUE".equals(record.getStatus())) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "该图书已逾期，续借前需要缴纳逾期费用。是否继续？",
                    "逾期警告", JOptionPane.YES_NO_OPTION);

            return choice == JOptionPane.YES_OPTION;
        }

        return true;
    }

    private void updateButtonStates() {
        // 更新借阅操作按钮状态
        int borrowSelectedRow = borrowTable.getSelectedRow();
        renewButton.setEnabled(borrowSelectedRow >= 0);
        returnButton.setEnabled(borrowSelectedRow >= 0);

        // 更新预约操作按钮状态
        int reservationSelectedRow = reservationTable.getSelectedRow();
        cancelReservationButton.setEnabled(reservationSelectedRow >= 0);

        boolean canBorrowReserved = false;
        if (reservationSelectedRow >= 0) {
            Reservation selectedReservation = reservations.get(reservationSelectedRow);
            canBorrowReserved = "ACTIVE".equals(selectedReservation.getStatus());
        }
        borrowReservedButton.setEnabled(canBorrowReserved);
    }

   @Override
    public void refresh() {
        loadUserData();
        loadBorrowRecords(currentUser.getCid());
        loadReservations(currentUser.getCid());
    }
}