// CartDAO.java
package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.util.DatabaseUtil;
import com.seu.vcampus.common.model.CartItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    public List<CartItem> getCartItemsByUserId(Integer userId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT * FROM CartItem WHERE UserId = ? ORDER BY AddDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                CartItem item = new CartItem();
                item.setId(rs.getInt("ID"));
                item.setUserId(rs.getInt("UserId"));
                item.setProductId(rs.getInt("ProductId"));
                item.setProductName(rs.getString("ProductName"));
                item.setQuantity(rs.getInt("Quantity"));
                item.setPrice(rs.getBigDecimal("Price"));
                item.setAddDate(rs.getTimestamp("AddDate"));

                cartItems.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cartItems;
    }

    public boolean addToCart(CartItem cartItem) {
        String checkSql = "SELECT * FROM CartItem WHERE UserId = ? AND ProductId = ?";
        String updateSql = "UPDATE CartItem SET Quantity = Quantity + ?, AddDate = ? WHERE UserId = ? AND ProductId = ?";
        String insertSql = "INSERT INTO CartItem (UserId, ProductId, ProductName, Quantity, Price, AddDate) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, cartItem.getUserId());
            checkStmt.setInt(2, cartItem.getProductId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, cartItem.getQuantity());
                    updateStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                    updateStmt.setInt(3, cartItem.getUserId());
                    updateStmt.setInt(4, cartItem.getProductId());

                    int affectedRows = updateStmt.executeUpdate();
                    return affectedRows > 0;
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, cartItem.getUserId());
                    insertStmt.setInt(2, cartItem.getProductId());
                    insertStmt.setString(3, cartItem.getProductName());
                    insertStmt.setInt(4, cartItem.getQuantity());
                    insertStmt.setBigDecimal(5, cartItem.getPrice());
                    insertStmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

                    int affectedRows = insertStmt.executeUpdate();
                    return affectedRows > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCartItemQuantity(Integer userId, Integer productId, int quantity) {
        String sql = "UPDATE CartItem SET Quantity = ? WHERE UserId = ? AND ProductId = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, productId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeFromCart(Integer userId, Integer productId) {
        String sql = "DELETE FROM CartItem WHERE UserId = ? AND ProductId = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearCart(Integer userId) {
        String sql = "DELETE FROM CartItem WHERE UserId = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public CartItem getCartItem(Integer userId, Integer productId) {
        CartItem item = null;
        String sql = "SELECT * FROM CartItem WHERE UserId = ? AND ProductId = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                item = new CartItem();
                item.setId(rs.getInt("ID"));
                item.setUserId(rs.getInt("UserId"));
                item.setProductId(rs.getInt("ProductId"));
                item.setProductName(rs.getString("ProductName"));
                item.setQuantity(rs.getInt("Quantity"));
                item.setPrice(rs.getBigDecimal("Price"));
                item.setAddDate(rs.getTimestamp("AddDate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return item;
    }
}