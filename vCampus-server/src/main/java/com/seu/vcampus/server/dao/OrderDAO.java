// OrderDAO.java
package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Order;
import com.seu.vcampus.common.util.DatabaseUtil;
import com.seu.vcampus.common.model.OrderItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public boolean createOrder(Order order, List<OrderItem> items) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // 插入订单
            String orderSql = "INSERT INTO [Order] (OrderId, UserId, OrderDate, TotalAmount, Status) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(orderSql)) {
                pstmt.setInt(1, order.getOrderId());
                pstmt.setInt(2, order.getUserId());
                pstmt.setTimestamp(3, new Timestamp(order.getOrderDate().getTime()));
                pstmt.setBigDecimal(4, order.getTotalAmount());
                pstmt.setString(5, order.getStatus());
                pstmt.setString(6, order.getShippingAddress());
                pstmt.setString(7, order.getContactPhone());

                pstmt.executeUpdate();
            }

            // 插入订单项
            String itemSql = "INSERT INTO OrderItem (OrderId, ProductId, ProductName, Price, Quantity, Subtotal) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(itemSql)) {
                for (OrderItem item : items) {
                    pstmt.setInt(1, order.getOrderId());
                    pstmt.setString(2, item.getProductId());
                    pstmt.setString(3, item.getProductName());
                    pstmt.setBigDecimal(4, item.getPrice());
                    pstmt.setInt(5, item.getQuantity());
                    pstmt.setBigDecimal(6, item.getSubtotal());

                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Order> getOrdersByUserId(Integer userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM [Order] WHERE UserId = ? ORDER BY OrderDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("OrderId"));
                order.setUserId(rs.getInt("UserId"));
                order.setOrderDate(rs.getTimestamp("OrderDate"));
                order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                order.setStatus(rs.getString("Status"));

                // 获取订单项
                order.setItems(getOrderItems(order.getOrderId()));

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    private List<OrderItem> getOrderItems(Integer orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM OrderItem WHERE OrderId = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getInt("ID"));
                item.setOrderId(rs.getInt("OrderId"));
                item.setProductId(rs.getString("ProductId"));
                item.setProductName(rs.getString("ProductName"));
                item.setPrice(rs.getBigDecimal("Price"));
                item.setQuantity(rs.getInt("Quantity"));
                item.setSubtotal(rs.getBigDecimal("Subtotal"));

                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public boolean updateOrderStatus(Integer orderId, String status) {
        String sql = "UPDATE [Order] SET Status = ? WHERE OrderId = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Order getOrderById(Integer orderId) {
        Order order = null;
        String sql = "SELECT * FROM [Order] WHERE OrderId = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                order = new Order();
                order.setOrderId(rs.getInt("OrderId"));
                order.setUserId(rs.getInt("UserId"));
                order.setOrderDate(rs.getTimestamp("OrderDate"));
                order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                order.setStatus(rs.getString("Status"));
                order.setItems(getOrderItems(orderId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }
}