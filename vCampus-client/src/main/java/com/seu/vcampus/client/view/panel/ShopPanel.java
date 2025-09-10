package com.seu.vcampus.client.view.panel;
import com.seu.vcampus.client.controller.ShopController;
import com.seu.vcampus.common.model.Product;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.client.view.frame.CartDialog;
import com.seu.vcampus.client.view.frame.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ShopPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton purchaseButton;
    private JSpinner quantitySpinner;
    private JButton viewCartButton;
    private JLabel cartItemCountLabel;
    private ShopController shopController;
    private User currentUser;

    public ShopPanel() {
        this.shopController = new ShopController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());

        // 标题
        JLabel titleLabel = new JLabel("校园商店", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // 购物车图标和数量显示
        JPanel cartPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cartItemCountLabel = new JLabel("0");
        cartItemCountLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        cartItemCountLabel.setForeground(Color.RED);

        JButton cartIconButton = new JButton("🛒");
        cartIconButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        cartIconButton.setBorder(BorderFactory.createEmptyBorder());
        cartIconButton.setContentAreaFilled(false);
        cartIconButton.addActionListener(e -> viewCart());

        cartPanel.add(cartItemCountLabel);
        cartPanel.add(cartIconButton);
        topPanel.add(cartPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // 产品表格
        String[] columnNames = {"ID", "商品名称", "描述", "价格", "库存", "分类"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Double.class;
                if (columnIndex == 4) return Integer.class;
                return String.class;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);
        productTable.setRowHeight(30);

        // 设置列宽
        productTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        // 底部面板
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel quantityLabel = new JLabel("数量:");
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        JButton addToCartButton = new JButton("加入购物车");
        addToCartButton.addActionListener(e -> addToCart());

        purchaseButton = new JButton("立即购买");
        purchaseButton.addActionListener(e -> purchaseItems());

        viewCartButton = new JButton("查看购物车");
        viewCartButton.addActionListener(e -> viewCart());

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshProducts());

        JButton backButton = new JButton("返回主界面");
        backButton.addActionListener(e -> goBackToMain());

        bottomPanel.add(quantityLabel);
        bottomPanel.add(quantitySpinner);
        bottomPanel.add(addToCartButton);
        bottomPanel.add(purchaseButton);
        bottomPanel.add(viewCartButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateCartItemCount();
    }

    public void refreshProducts() {
        SwingWorker<List<Product>, Void> worker = new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                return shopController.getAvailableProducts();
            }

            @Override
            protected void done() {
                try {
                    List<Product> products = get();
                    tableModel.setRowCount(0);

                    if (products != null && !products.isEmpty()) {
                        for (Product product : products) {
                            Object[] row = {
                                    product.getProductId(),
                                    product.getProductName(),
                                    product.getDescription(),
                                    product.getPrice(),
                                    product.getStock(),
                                    product.getCategory()
                            };
                            tableModel.addRow(row);
                        }
                    } else {
                        JOptionPane.showMessageDialog(ShopPanel.this,
                                "暂无商品",
                                "提示",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ShopPanel.this,
                            "加载商品失败: " + ex.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void addToCart() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "请先登录系统");
            return;
        }

        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一件商品");
            return;
        }

        int productId = (Integer) tableModel.getValueAt(selectedRow, 0);
        int quantity = (Integer) quantitySpinner.getValue();
        int stock = (Integer) tableModel.getValueAt(selectedRow, 4);

        if (quantity > stock) {
            JOptionPane.showMessageDialog(this, "库存不足");
            return;
        }

        boolean success = shopController.addToCart(productId, quantity);

        if (success) {
            JOptionPane.showMessageDialog(this, "已添加到购物车");
            updateCartItemCount();
        } else {
            JOptionPane.showMessageDialog(this, "添加失败");
        }
    }

    private void purchaseItems() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "请先登录系统");
            return;
        }

        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一件商品");
            return;
        }

        int productId = (Integer) tableModel.getValueAt(selectedRow, 0);
        int quantity = (Integer) quantitySpinner.getValue();
        int stock = (Integer) tableModel.getValueAt(selectedRow, 4);

        if (quantity > stock) {
            JOptionPane.showMessageDialog(this, "库存不足");
            return;
        }

        double totalPrice = (Double) tableModel.getValueAt(selectedRow, 3) * quantity;
        int confirm = JOptionPane.showConfirmDialog(this,
                "确认购买 " + quantity + " 件 '" +
                        tableModel.getValueAt(selectedRow, 1) + "'? 总价: " +
                        totalPrice + "元",
                "确认购买", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = shopController.purchaseProduct(currentUser.getUserId(), productId, quantity);

            if (success) {
                JOptionPane.showMessageDialog(this, "购买成功");
                refreshProducts(); // 刷新商品列表
                updateCartItemCount();
            } else {
                JOptionPane.showMessageDialog(this, "购买失败，请重试");
            }
        }
    }

    private void viewCart() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "请先登录系统");
            return;
        }

        if (shopController.getCartItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "购物车为空");
            return;
        }

        // 打开购物车对话框
        CartDialog cartDialog = new CartDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                shopController,
                currentUser.getUserId()
        );
        cartDialog.setVisible(true);

        // 刷新商品列表和购物车数量
        refreshProducts();
        updateCartItemCount();
    }

    private void updateCartItemCount() {
        int count = shopController.getCartItemCount();
        cartItemCountLabel.setText(String.valueOf(count));
    }

    private void goBackToMain() {
        MainFrame.getInstance().showMainPanel(currentUser);
    }

    public void clearCart() {
        shopController.clearCart();
        updateCartItemCount();
    }
}