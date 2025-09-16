package com.seu.vcampus.client.view.panel;

import com.seu.vcampus.client.controller.ShopController;
import com.seu.vcampus.common.model.CartItem;
import com.seu.vcampus.common.model.Product;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.client.view.frame.CartDialog;
import com.seu.vcampus.client.view.frame.MainFrame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;

public class ShopPanel extends JPanel {
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton purchaseButton;
    private JSpinner quantitySpinner;
    private JButton viewCartButton;
    private JLabel cartItemCountLabel;
    private ShopController shopController;
    private User currentUser;
    private JButton refreshButton;
    private JComboBox<String> categoryFilter;
    private JTextField searchField;
    private JButton searchButton;
    private List<Product> allProducts; // 缓存所有商品

    public ShopPanel() {
        this.shopController = new ShopController();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 标题
        JLabel titleLabel = new JLabel("校园商店", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // 搜索和筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        // 分类筛选
        filterPanel.add(new JLabel("分类:"));
        categoryFilter = new JComboBox<>();
        categoryFilter.addItem("所有分类");
        categoryFilter.addActionListener(e -> filterProducts());
        filterPanel.add(categoryFilter);

        // 搜索框
        filterPanel.add(new JLabel("搜索:"));
        searchField = new JTextField(15);
        filterPanel.add(searchField);

        searchButton = new JButton("搜索");
        searchButton.addActionListener(e -> filterProducts());
        filterPanel.add(searchButton);

        topPanel.add(filterPanel, BorderLayout.SOUTH);

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
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel quantityLabel = new JLabel("数量:");
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        JButton addToCartButton = new JButton("加入购物车");
        addToCartButton.addActionListener(e -> addToCart());

        purchaseButton = new JButton("立即购买");
        purchaseButton.addActionListener(e -> purchaseItems());

        viewCartButton = new JButton("查看购物车");
        viewCartButton.addActionListener(e -> viewCart());

        refreshButton = new JButton("刷新");
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

        // 初始加载商品
        refreshProducts();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateCartItemCount();
    }

    public void refreshProducts() {
        // 显示加载状态
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        refreshButton.setEnabled(false);

        SwingWorker<List<Product>, Void> worker = new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                return shopController.getAllProducts();
            }

            @Override
            protected void done() {
                try {
                    allProducts = get();
                    tableModel.setRowCount(0);

                    if (allProducts != null && !allProducts.isEmpty()) {
                        for (Product product : allProducts) {
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
                        updateCategoryFilter();
                    } else {
                        JOptionPane.showMessageDialog(ShopPanel.this,
                                "暂无商品",
                                "提示",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    String errorMessage = ex.getMessage();
                    if (errorMessage.contains("无法连接到服务器")) {
                        JOptionPane.showMessageDialog(ShopPanel.this,
                                "无法连接到服务器。\n请确保服务器正在运行并监听端口8888",
                                "连接错误",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(ShopPanel.this,
                                "加载商品失败: " + errorMessage,
                                "错误",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    refreshButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void updateCategoryFilter() {
        if (allProducts == null) return;

        // 收集所有分类
        Set<String> categories = new TreeSet<>();
        for (Product product : allProducts) {
            if (product.getCategory() != null && !product.getCategory().isEmpty()) {
                categories.add(product.getCategory());
            }
        }

        // 更新分类筛选器
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        categoryFilter.removeAllItems();
        categoryFilter.addItem("所有分类");

        for (String category : categories) {
            categoryFilter.addItem(category);
        }

        // 恢复之前的选择
        if (selectedCategory != null) {
            categoryFilter.setSelectedItem(selectedCategory);
        } else {
            categoryFilter.setSelectedIndex(0);
        }
    }

    private void filterProducts() {
        if (allProducts == null) return;

        String selectedCategory = (String) categoryFilter.getSelectedItem();
        String searchText = searchField.getText().toLowerCase();

        tableModel.setRowCount(0);

        for (Product product : allProducts) {
            // 分类筛选
            if (!"所有分类".equals(selectedCategory) &&
                    !Objects.equals(selectedCategory, product.getCategory())) {
                continue;
            }

            // 关键词搜索
            if (!searchText.isEmpty()) {
                boolean matches = product.getProductName().toLowerCase().contains(searchText) ||
                        (product.getDescription() != null &&
                                product.getDescription().toLowerCase().contains(searchText)) ||
                        (product.getCategory() != null &&
                                product.getCategory().toLowerCase().contains(searchText));
                if (!matches) continue;
            }

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

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "没有找到符合条件的商品",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
        }
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

        String productId = (String) tableModel.getValueAt(selectedRow, 0);
        int quantity = (Integer) quantitySpinner.getValue();
        int stock = (Integer) tableModel.getValueAt(selectedRow, 4);

        if (quantity > stock) {
            JOptionPane.showMessageDialog(this, "库存不足");
            return;
        }

        // 显示加载状态
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return shopController.addToCart(currentUser.getUserId(), productId, quantity);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(ShopPanel.this, "已添加到购物车");
                        updateCartItemCount();
                    } else {
                        JOptionPane.showMessageDialog(ShopPanel.this, "添加失败");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ShopPanel.this,
                            "添加失败: " + e.getMessage(),
                            "错误", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };

        worker.execute();
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

        String productId = (String) tableModel.getValueAt(selectedRow, 0);
        int quantity = (Integer) quantitySpinner.getValue();
        int stock = (Integer) tableModel.getValueAt(selectedRow, 4);

        if (quantity > stock) {
            JOptionPane.showMessageDialog(this, "库存不足");
            return;
        }

        BigDecimal price = (BigDecimal) tableModel.getValueAt(selectedRow, 3);
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));
        String productName = (String) tableModel.getValueAt(selectedRow, 1);

        // 创建配送信息输入对话框
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();

        panel.add(new JLabel("配送地址:"));
        panel.add(addressField);
        panel.add(new JLabel("联系电话:"));
        panel.add(phoneField);
        panel.add(new JLabel("总金额:"));
        panel.add(new JLabel(totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " 元"));

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
                "确认购买 " + quantity + " 件 '" + productName +
                        "'? 总价: " + totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP) + "元",
                "确认购买", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // 显示加载状态
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    // 先添加到购物车
                    boolean added = shopController.addToCart(currentUser.getUserId(), productId, quantity);
                    if (!added) return false;

                    // 然后创建订单
                    return shopController.createOrder(currentUser.getUserId(), shippingAddress, contactPhone);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(ShopPanel.this, "购买成功");
                            refreshProducts(); // 刷新商品列表
                            updateCartItemCount();
                        } else {
                            JOptionPane.showMessageDialog(ShopPanel.this, "购买失败，请重试");
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(ShopPanel.this,
                                "购买失败: " + e.getMessage(),
                                "错误", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            };

            worker.execute();
        }
    }

    private void viewCart() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "请先登录系统");
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
        if (currentUser == null) {
            cartItemCountLabel.setText("0");
            return;
        }

        // 使用异步方式获取购物车数量
        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                List<CartItem> cartItems =
                        shopController.getCartItems(currentUser.getUserId());
                int count = 0;
                for (CartItem item : cartItems) {
                    count += item.getQuantity();
                }
                return count;
            }

            @Override
            protected void done() {
                try {
                    int count = get();
                    cartItemCountLabel.setText(String.valueOf(count));
                } catch (Exception e) {
                    cartItemCountLabel.setText("0");
                }
            }
        };

        worker.execute();
    }

    private void goBackToMain() {
        MainFrame.getInstance().showMainPanel(currentUser);
    }

    public void clearCart() {
        if (currentUser != null) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return shopController.clearCart(currentUser.getUserId());
                }

                @Override
                protected void done() {
                    try {
                        get(); // 等待操作完成
                        updateCartItemCount();
                    } catch (Exception e) {
                        // 忽略错误
                    }
                }
            };

            worker.execute();
        }
    }
}