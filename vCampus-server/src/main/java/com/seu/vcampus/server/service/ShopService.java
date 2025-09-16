// ShopService.java
package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.Order;
import com.seu.vcampus.server.dao.CartDAO;
import com.seu.vcampus.server.dao.OrderDAO;
import com.seu.vcampus.server.dao.ProductDAO;
import com.seu.vcampus.common.model.CartItem;
import com.seu.vcampus.common.model.OrderItem;
import com.seu.vcampus.common.model.Product;
import com.seu.vcampus.common.util.DatabaseUtil;

import java.sql.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShopService {
    private ProductDAO productDAO;
    private CartDAO cartDAO;
    private OrderDAO orderDAO;

    public ShopService() {
        productDAO = new ProductDAO();
        cartDAO = new CartDAO();
        orderDAO = new OrderDAO();
    }

    // 商品相关方法
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("执行SQL查询: " + sql);

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("ID"));
                product.setProductId(rs.getString("ProductId"));
                product.setProductName(rs.getString("ProductName"));
                product.setDescription(rs.getString("Description"));
                product.setPrice(rs.getBigDecimal("Price"));
                product.setStock(rs.getInt("Stock"));
                product.setCategory(rs.getString("Category"));
                product.setImageURL(rs.getString("ImageURL"));
                product.setLocation(rs.getString("Location"));

                products.add(product);
            }

            System.out.println("成功获取 " + products.size() + " 个商品");
        } catch (SQLException e) {
            System.err.println("数据库查询失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return products;
    }

    public Product getProductById(String productId) {
        return productDAO.getProductById(productId);
    }

    public List<Product> getProductsByCategory(String category) {
        return productDAO.getProductsByCategory(category);
    }

    public boolean addProduct(Product product) {
        return productDAO.addProduct(product);
    }

    public boolean updateProduct(Product product) {
        return productDAO.updateProduct(product);
    }

    public boolean deleteProduct(String productId) {
        return productDAO.deleteProduct(productId);
    }

    // 购物车相关方法
    public List<CartItem> getCartItems(Integer userId) {
        return cartDAO.getCartItemsByUserId(userId);
    }

    public boolean addToCart(CartItem cartItem) {
        return cartDAO.addToCart(cartItem);
    }

    // 购物车相关方法需要处理String类型的productId
    public boolean addToCart(Integer userId, String productId, int quantity) {
        Product product = productDAO.getProductById(productId);
        if (product == null) {
            return false;
        }

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);  // 使用String类型
        cartItem.setProductName(product.getProductName());
        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getPrice());

        return cartDAO.addToCart(cartItem);
    }

    public boolean updateCartItemQuantity(Integer userId, String productId, int quantity) {
        return cartDAO.updateCartItemQuantity(userId, productId, quantity);
    }

    public boolean removeFromCart(Integer userId, String productId) {
        return cartDAO.removeFromCart(userId, productId);
    }

    public boolean clearCart(Integer userId) {
        return cartDAO.clearCart(userId);
    }

    public CartItem getCartItem(Integer userId, String productId) {
        return cartDAO.getCartItem(userId, productId);
    }

    // 计算购物车总价
    public BigDecimal calculateCartTotal(List<CartItem> cartItems) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }

    // 订单相关方法
    public List<Order> getUserOrders(Integer userId) {
        return orderDAO.getOrdersByUserId(userId);
    }

    public Order getOrderById(Integer orderId) {
        return orderDAO.getOrderById(orderId);
    }

    public boolean createOrderFromCart(Integer userId) {
        // 获取购物车中的商品
        List<CartItem> cartItems = cartDAO.getCartItemsByUserId(userId);
        if (cartItems.isEmpty()) {
            return false;
        }

        // 检查库存
        for (CartItem cartItem : cartItems) {
            Product product = productDAO.getProductById(String.valueOf(cartItem.getProductId()));
            if (product == null || product.getStock() < cartItem.getQuantity()) {
                return false;
            }
        }

        // 创建订单对象
        Order order = new Order();
        order.setOrderId(generateOrderId());
        order.setUserId(userId);
        order.setOrderDate(new Date());
        order.setStatus("待付款");

        // 计算订单总金额并创建订单项
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());

            BigDecimal subtotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);   // ← BigDecimal 累加
        }

        order.setTotalAmount(totalAmount);

        // 保存订单
        boolean success = orderDAO.createOrder(order, orderItems);
        if (success) {
            // 减少库存
            for (CartItem cartItem : cartItems) {
                productDAO.updateProductStock(String.valueOf(cartItem.getProductId()), cartItem.getQuantity());
            }

            // 清空购物车
            cartDAO.clearCart(userId);
        }

        return success;
    }

    public boolean updateOrderStatus(Integer orderId, String status) {
        return orderDAO.updateOrderStatus(orderId, status);
    }

    // 生成唯一的订单ID
    private Integer generateOrderId() {
        return (int) (System.currentTimeMillis() % 1000000);
    }
}