// ShopService.java
package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.shop.Order;
import com.seu.vcampus.server.dao.shop.CartDao;
import com.seu.vcampus.server.dao.shop.OrderDao;
import com.seu.vcampus.server.dao.shop.ProductDao;
import com.seu.vcampus.common.model.shop.CartItem;
import com.seu.vcampus.common.model.shop.OrderItem;
import com.seu.vcampus.common.model.shop.Product;
import com.seu.vcampus.common.util.DBConnector;

import java.sql.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShopService {
    private ProductDao productDao;
    private CartDao cartDao;
    private OrderDao orderDao;

    public ShopService() {
        productDao = new ProductDao();
        cartDao = new CartDao();
        orderDao = new OrderDao();
    }

    // 商品相关方法
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product";

        try (Connection conn = DBConnector.getConnection();
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
        return productDao.getProductById(productId);
    }

    public List<Product> getProductsByCategory(String category) {
        return productDao.getProductsByCategory(category);
    }

    public boolean addProduct(Product product) {
        return productDao.addProduct(product);
    }

    public boolean updateProduct(Product product) {
        return productDao.updateProduct(product);
    }

    public boolean deleteProduct(String productId) {
        return productDao.deleteProduct(productId);
    }

    // 购物车相关方法
    public List<CartItem> getCartItems(Integer userId) {
        return cartDao.getCartItemsByUserId(userId);
    }

    public boolean addToCart(CartItem cartItem) {
        return cartDao.addToCart(cartItem);
    }

    // 购物车相关方法需要处理String类型的productId
    public boolean addToCart(Integer userId, String productId, int quantity) {
        Product product = productDao.getProductById(productId);
        if (product == null) {
            return false;
        }

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);  // 使用String类型
        cartItem.setProductName(product.getProductName());
        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getPrice());

        return cartDao.addToCart(cartItem);
    }

    public boolean updateCartItemQuantity(Integer userId, String productId, int quantity) {
        return cartDao.updateCartItemQuantity(userId, productId, quantity);
    }

    public boolean removeFromCart(Integer userId, String productId) {
        return cartDao.removeFromCart(userId, productId);
    }

    public boolean clearCart(Integer userId) {
        return cartDao.clearCart(userId);
    }

    public CartItem getCartItem(Integer userId, String productId) {
        return cartDao.getCartItem(userId, productId);
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
        return orderDao.getOrdersByUserId(userId);
    }

    public Order getOrderById(Integer orderId) {
        return orderDao.getOrderById(orderId);
    }

    public boolean createOrderFromCart(Integer userId) {
        // 获取购物车中的商品
        List<CartItem> cartItems = cartDao.getCartItemsByUserId(userId);
        if (cartItems.isEmpty()) {
            return false;
        }

        // 检查库存
        for (CartItem cartItem : cartItems) {
            Product product = productDao.getProductById(String.valueOf(cartItem.getProductId()));
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
        boolean success = orderDao.createOrder(order, orderItems);
        if (success) {
            // 减少库存
            for (CartItem cartItem : cartItems) {
                productDao.updateProductStock(String.valueOf(cartItem.getProductId()), cartItem.getQuantity());
            }

            // 清空购物车
            cartDao.clearCart(userId);
        }

        return success;
    }

    public boolean updateOrderStatus(Integer orderId, String status) {
        return orderDao.updateOrderStatus(orderId, status);
    }

    // 生成唯一的订单ID
    private Integer generateOrderId() {
        return (int) (System.currentTimeMillis() % 1000000);
    }
}