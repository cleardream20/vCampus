package com.seu.vcampus.client.view.frame;

import com.seu.vcampus.client.controller.ShopController;
import com.seu.vcampus.common.model.CartItem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CartDialog extends JDialog {
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JButton purchaseButton;
    private JButton removeButton;
    private JButton clearButton;
    private ShopController shopController;
    private int userId;

    public CartDialog(JFrame parent, ShopController shopController, int userId) {
        super(parent, "购物车", true);
        this.shopController = shopController;
        this.userId = userId;

        initComponents();
        loadCartItems();
        setupDialog();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 标题
        JLabel titleLabel = new JLabel("我的购物车", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // 购物车表格
        String[] columnNames = {"商品名称", "单价", "数量", "总价"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1 || columnIndex == 3) return Double.class;
                if (columnIndex == 2) return Integer.class;
                return String.class;
            }
        };

        cartTable = new JTable(tableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setRowHeight(30);

        // 设置列宽
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(cartTable);
        add(scrollPane, BorderLayout.CENTER);

        // 底部按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

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

        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(purchaseButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupDialog() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
    }

    private void loadCartItems() {
        List<CartItem> cartItems = shopController.getCartItems();
        tableModel.setRowCount(0);

        double total = 0;
        for (CartItem item : cartItems) {
            double itemTotal = item.getTotalPrice();
            Object[] row = {
                    item.getProductName(),
                    item.getPrice(),
                    item.getQuantity(),
                    itemTotal
            };
            tableModel.addRow(row);
            total += itemTotal;
        }

        // 添加总计行
        tableModel.addRow(new Object[]{"", "", "总计:", total});

        // 更新按钮状态
        boolean hasItems = !cartItems.isEmpty();
        purchaseButton.setEnabled(hasItems);
        clearButton.setEnabled(hasItems);
        removeButton.setEnabled(hasItems);
    }

    private void removeSelectedItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1 || selectedRow >= tableModel.getRowCount() - 1) {
            JOptionPane.showMessageDialog(this, "请选择要移除的商品");
            return;
        }

        // 获取商品ID（需要从控制器中获取，因为表格中只显示名称）
        List<CartItem> cartItems = shopController.getCartItems();
        if (selectedRow >= cartItems.size()) {
            return;
        }

        int productId = cartItems.get(selectedRow).getProductId();
        boolean success = shopController.removeFromCart(productId);

        if (success) {
            JOptionPane.showMessageDialog(this, "已从购物车移除");
            loadCartItems();
        } else {
            JOptionPane.showMessageDialog(this, "移除失败");
        }
    }

    private void clearCart() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "确认清空购物车?",
                "确认清空", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            shopController.clearCart();
            loadCartItems();
            JOptionPane.showMessageDialog(this, "购物车已清空");
        }
    }

    private void purchaseAllItems() {
        double total = shopController.getCartTotal();
        int confirm = JOptionPane.showConfirmDialog(this,
                "确认购买购物车中的所有商品? 总价: " + total + "元",
                "确认购买", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = shopController.purchaseAllInCart(userId);

            if (success) {
                JOptionPane.showMessageDialog(this, "购买成功");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "购买失败，请重试");
            }
        }
    }
}