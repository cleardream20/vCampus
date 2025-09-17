package com.seu.vcampus.client.view.panel.shop;

import com.seu.vcampus.client.controller.ShopController;
import com.seu.vcampus.common.model.shop.CartItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

public class CartDialog extends JDialog {
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JButton purchaseButton;
    private JButton removeButton;
    private JButton clearButton;
    private JButton refreshButton;
    private JLabel totalLabel;
    private ShopController shopController;
    private String userId;
    private List<CartItem> currentCartItems;

    public CartDialog(JFrame parent, ShopController shopController, String userId) {
        super(parent, "购物车", true);
        this.shopController = shopController;
        this.userId = userId;

        initComponents();
        loadCartItems();
        setupDialog();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // 标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("我的购物车", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 购物车表格
        String[] columnNames = {"商品ID", "商品名称", "单价", "数量", "小计"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2 || columnIndex == 4) return Double.class;
                if (columnIndex == 3) return Integer.class;
                return String.class;
            }
        };

        cartTable = new JTable(tableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setRowHeight(30);
        cartTable.getTableHeader().setReorderingAllowed(false);

        // 设置列宽
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.setPreferredSize(new Dimension(500, 250));
        add(scrollPane, BorderLayout.CENTER);

        // 底部面板
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        // 总计面板
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalLabel = new JLabel("总计: 0.00 元");
        totalLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        totalPanel.add(totalLabel);
        bottomPanel.add(totalPanel, BorderLayout.NORTH);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        refreshButton = new JButton("刷新");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCartItems();
            }
        });

        removeButton = new JButton("移除选中");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedItem();
            }
        });

        clearButton = new JButton("清空购物车");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearCart();
            }
        });

        purchaseButton = new JButton("结算购买");
        purchaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                purchaseAllItems();
            }
        });

        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(refreshButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(purchaseButton);
        buttonPanel.add(closeButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupDialog() {
        pack();
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void loadCartItems() {
        // 显示加载状态
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        refreshButton.setEnabled(false);

        // 使用SwingWorker进行异步加载
        SwingWorker<List<CartItem>, Void> worker = new SwingWorker<List<CartItem>, Void>() {
            @Override
            protected List<CartItem> doInBackground() throws Exception {
                return shopController.getCartItems(userId);
            }

            @Override
            protected void done() {
                try {
                    currentCartItems = get();
                    updateCartTable();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(CartDialog.this,
                            "加载购物车失败: " + e.getMessage(),
                            "错误", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    refreshButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void removeSelectedItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要移除的商品", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String productId = (String) tableModel.getValueAt(selectedRow, 0);
        String productName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "确认从购物车中移除 '" + productName + "'?",
                "确认移除", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // 显示加载状态
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            removeButton.setEnabled(false);

            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return shopController.removeFromCart(userId, productId);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(CartDialog.this,
                                    "已从购物车移除",
                                    "成功", JOptionPane.INFORMATION_MESSAGE);
                            loadCartItems();
                        } else {
                            JOptionPane.showMessageDialog(CartDialog.this,
                                    "移除失败",
                                    "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(CartDialog.this,
                                "移除失败: " + e.getMessage(),
                                "错误", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                        removeButton.setEnabled(true);
                    }
                }
            };

            worker.execute();
        }
    }

    private void clearCart() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "确认清空购物车?",
                "确认清空", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // 显示加载状态
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            clearButton.setEnabled(false);

            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return shopController.clearCart(userId);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(CartDialog.this,
                                    "购物车已清空",
                                    "成功", JOptionPane.INFORMATION_MESSAGE);
                            loadCartItems();
                        } else {
                            JOptionPane.showMessageDialog(CartDialog.this,
                                    "清空失败",
                                    "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(CartDialog.this,
                                "清空失败: " + e.getMessage(),
                                "错误", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                        clearButton.setEnabled(true);
                    }
                }
            };

            worker.execute();
        }
    }

    private void purchaseAllItems() {
        if (currentCartItems == null || currentCartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "购物车为空，无法结算", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 计算总价
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : currentCartItems) {
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }

        // 创建配送信息输入对话框
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();

        panel.add(new JLabel("配送地址:"));
        panel.add(addressField);
        panel.add(new JLabel("联系电话:"));
        panel.add(phoneField);
        panel.add(new JLabel("总金额:"));
        panel.add(new JLabel(total.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " 元"));

        int option = JOptionPane.showConfirmDialog(this, panel, "请输入配送信息",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option != JOptionPane.OK_OPTION) {
            return; // 用户取消
        }

        String shippingAddress = addressField.getText().trim();
        String contactPhone = phoneField.getText().trim();

        if (shippingAddress.isEmpty() || contactPhone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "配送地址和联系电话不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确认购买购物车中的所有商品? 总价: " + total.setScale(2, BigDecimal.ROUND_HALF_UP) + " 元",
                "确认购买", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // 显示加载状态
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            purchaseButton.setEnabled(false);

            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return shopController.createOrder(userId, shippingAddress, contactPhone);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(CartDialog.this,
                                    "购买成功",
                                    "成功", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(CartDialog.this,
                                    "购买失败，请检查库存或余额",
                                    "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(CartDialog.this,
                                "购买失败: " + e.getMessage(),
                                "错误", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                        purchaseButton.setEnabled(true);
                    }
                }
            };

            worker.execute();
        }
    }

    private void updateCartTable() {
        tableModel.setRowCount(0);

        if (currentCartItems == null || currentCartItems.isEmpty()) {
            totalLabel.setText("总计: 0.00 元");
            updateButtonStates(false);
            return;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : currentCartItems) {
            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            Object[] row = {
                    item.getProductId(),
                    item.getProductName(),
                    item.getPrice(),
                    item.getQuantity(),
                    subtotal
            };
            tableModel.addRow(row);
            total = total.add(subtotal);
        }

        totalLabel.setText("总计: " + total.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " 元");
        updateButtonStates(true);
    }

    private void updateButtonStates(boolean hasItems) {
        purchaseButton.setEnabled(hasItems);
        clearButton.setEnabled(hasItems);
        removeButton.setEnabled(hasItems && cartTable.getSelectedRow() != -1);
    }


}