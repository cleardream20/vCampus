package com.seu.vcampus.server.dao.shop;

import com.seu.vcampus.common.model.shop.Product;
import com.seu.vcampus.common.model.shop.CartItem;
import com.seu.vcampus.common.model.shop.Order;
import com.seu.vcampus.common.model.shop.OrderItem;
import com.seu.vcampus.common.util.DBConnector;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShopDao {

    public List<Product> getAvailableProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE Stock > 0";

        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getString("ProductID"));
                product.setProductName(rs.getString("ProductName"));
                product.setDescription(rs.getString("Description"));
                product.setPrice(rs.getBigDecimal("Price"));
                product.setStock(rs.getInt("Stock"));
                product.setCategory(rs.getString("Category"));
                product.setImageURL(rs.getString("ImageURL"));

                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public Product getProductById(int productId) {
        String sql = "SELECT * FROM Products WHERE ProductID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getString("ProductID"));
                product.setProductName(rs.getString("ProductName"));
                product.setDescription(rs.getString("Description"));
                product.setPrice(rs.getBigDecimal("Price"));
                product.setStock(rs.getInt("Stock"));
                product.setCategory(rs.getString("Category"));
                product.setImageURL(rs.getString("ImageURL"));

                return product;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createOrder(int userId, int productId, int quantity, double price) {
        String orderSql = "INSERT INTO Orders (UserID, OrderDate, TotalAmount, Status) VALUES (?, GETDATE(), ?, '已完成')";
        String detailSql = "INSERT INTO OrderDetails (OrderID, ProductID, Quantity, Price) VALUES (?, ?, ?, ?)";
        String updateStockSql = "UPDATE Products SET Stock = Stock - ? WHERE ProductID = ?";

        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // 开始事务

            // 创建订单
            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, userId);
            orderStmt.setDouble(2, price * quantity);
            orderStmt.executeUpdate();

            // 获取生成的订单ID
            ResultSet rs = orderStmt.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            // 添加订单明细
            PreparedStatement detailStmt = conn.prepareStatement(detailSql);
            detailStmt.setInt(1, orderId);
            detailStmt.setInt(2, productId);
            detailStmt.setInt(3, quantity);
            detailStmt.setDouble(4, price);
            detailStmt.executeUpdate();

            // 更新库存
            PreparedStatement updateStockStmt = conn.prepareStatement(updateStockSql);
            updateStockStmt.setInt(1, quantity);
            updateStockStmt.setInt(2, productId);
            updateStockStmt.executeUpdate();

            conn.commit(); // 提交事务
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // 回滚事务
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // 恢复自动提交模式
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean createOrderFromCart(int userId, List<CartItem> items) {
        String orderSql = "INSERT INTO Orders (UserID, OrderDate, TotalAmount, Status) VALUES (?, GETDATE(), ?, '已完成')";
        String detailSql = "INSERT INTO OrderDetails (OrderID, ProductID, Quantity, Price) VALUES (?, ?, ?, ?)";
        String updateStockSql = "UPDATE Products SET Stock = Stock - ? WHERE ProductID = ?";

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : items) {
            BigDecimal subTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(subTotal);
        }

        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // 开始事务

            // 创建订单
            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, userId);
            orderStmt.setBigDecimal(2, totalAmount);
            orderStmt.executeUpdate();

            // 获取生成的订单ID
            ResultSet rs = orderStmt.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            // 添加所有订单明细
            PreparedStatement detailStmt = conn.prepareStatement(detailSql);
            for (CartItem item : items) {
                detailStmt.setInt(1, orderId);
                detailStmt.setString(2, item.getProductId());
                detailStmt.setInt(3, item.getQuantity());
                detailStmt.setBigDecimal(4, item.getPrice());
                detailStmt.addBatch();
            }
            detailStmt.executeBatch();

            // 更新所有商品库存
            PreparedStatement updateStockStmt = conn.prepareStatement(updateStockSql);
            for (CartItem item : items) {
                updateStockStmt.setInt(1, item.getQuantity());
                updateStockStmt.setString(2, item.getProductId());
                updateStockStmt.addBatch();
            }
            updateStockStmt.executeBatch();

            conn.commit(); // 提交事务
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // 回滚事务
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // 恢复自动提交模式
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean updateProductStock(int productId, int quantity) {
        String sql = "UPDATE Products SET Stock = Stock - ? WHERE ProductID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Order> getUserOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        String orderSql = "SELECT * FROM Orders WHERE UserID = ? ORDER BY OrderDate DESC";
        String detailSql = "SELECT od.*, p.ProductName FROM OrderDetails od " +
                "JOIN Products p ON od.ProductID = p.ProductID " +
                "WHERE od.OrderID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderSql)) {

            orderStmt.setInt(1, userId);
            ResultSet orderRs = orderStmt.executeQuery();

            while (orderRs.next()) {
                Order order = new Order();
                order.setOrderId(orderRs.getInt("OrderID"));
                order.setUserId(orderRs.getString("UserID"));
                order.setOrderDate(orderRs.getTimestamp("OrderDate"));
                order.setTotalAmount(orderRs.getBigDecimal("TotalAmount"));
                order.setStatus(orderRs.getString("Status"));

                // 获取订单明细
                List<OrderItem> items = new ArrayList<>();
                try (PreparedStatement detailStmt = conn.prepareStatement(detailSql)) {
                    detailStmt.setInt(1, order.getOrderId());
                    ResultSet detailRs = detailStmt.executeQuery();

                    while (detailRs.next()) {
                        OrderItem item = new OrderItem();
                        item.setProductId(detailRs.getString("ProductID"));
                        item.setProductName(detailRs.getString("ProductName"));
                        item.setPrice(detailRs.getBigDecimal("Price"));
                        item.setQuantity(detailRs.getInt("Quantity"));

                        items.add(item);
                    }
                }

                order.setItems(items);
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}