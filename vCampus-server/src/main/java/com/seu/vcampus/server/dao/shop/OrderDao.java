// OrderDAO.java
package com.seu.vcampus.server.dao.shop;

import com.seu.vcampus.common.model.shop.Orders;
import com.seu.vcampus.common.util.DBConnector;
import com.seu.vcampus.common.model.shop.OrderItem;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {

    public boolean createOrder(Orders orders, List<OrderItem> items) {
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false);

            // 插入订单
            String orderSql = "INSERT INTO Orders (OrderId, UserId, OrderDate, TotalAmount, Status, ShippingAddress, ContactPhone) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(orderSql)) {
                pstmt.setInt(1, orders.getOrderId());
                pstmt.setString(2, orders.getUserId());
                pstmt.setTimestamp(3, new Timestamp(orders.getOrderDate().getTime()));
                pstmt.setBigDecimal(4, orders.getTotalAmount());
                pstmt.setString(5, orders.getStatus());
                pstmt.setString(6, orders.getShippingAddress());
                pstmt.setString(7, orders.getContactPhone());

                pstmt.executeUpdate();
            }

            // 插入订单项
            String itemSql = "INSERT INTO OrderItem (OrderId, ProductId, ProductName, Price, Quantity, Subtotal) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(itemSql)) {
                for (OrderItem item : items) {
                    pstmt.setInt(1, orders.getOrderId());
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

    public List<Orders> getOrdersByUserId(String userId) {
        List<Orders> orders = new ArrayList<>();
        String sql = "SELECT * FROM [Order] WHERE UserId = ? ORDER BY OrderDate DESC";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Orders order = new Orders();
                order.setOrderId(rs.getInt("OrderId"));
                order.setUserId(rs.getString("UserId"));
                order.setOrderDate(rs.getTimestamp("OrderDate"));
                order.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                order.setStatus(rs.getString("Status"));
                order.setShippingAddress(rs.getString("ShippingAddress"));
                order.setContactPhone(rs.getString("ContactPhone"));

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

        try (Connection conn = DBConnector.getConnection();
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

        try (Connection conn = DBConnector.getConnection();
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

    public Orders getOrderById(Integer orderId) {
        Orders orders = null;
        String sql = "SELECT * FROM [Order] WHERE OrderId = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                orders = new Orders();
                orders.setOrderId(rs.getInt("OrderId"));
                orders.setUserId(rs.getString("UserId"));
                orders.setOrderDate(rs.getTimestamp("OrderDate"));
                orders.setTotalAmount(rs.getBigDecimal("TotalAmount"));
                orders.setStatus(rs.getString("Status"));
                orders.setShippingAddress(rs.getString("ShippingAddress"));
                orders.setContactPhone(rs.getString("ContactPhone"));
                orders.setItems(getOrderItems(orderId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
}