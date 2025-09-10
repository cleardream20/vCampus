package com.seu.vcampus.server.service;

import com.seu.vcampus.server.dao.ShopDAO;
import com.seu.vcampus.common.model.Product;
import com.seu.vcampus.common.model.CartItem;
import com.seu.vcampus.common.model.Order;

import java.util.List;

public class ShopService {
    private ShopDAO shopDAO;

    public ShopService() {
        this.shopDAO = new ShopDAO();
    }

    public List<Product> getAvailableProducts() {
        return shopDAO.getAvailableProducts();
    }

    public Product getProductById(int productId) {
        return shopDAO.getProductById(productId);
    }

    public boolean purchaseProduct(int userId, int productId, int quantity) {
        // 检查库存是否足够
        Product product = shopDAO.getProductById(productId);
        if (product == null || product.getStock() < quantity) {
            return false;
        }

        // 创建订单
        boolean success = shopDAO.createOrder(userId, productId, quantity, product.getPrice());

        if (success) {
            // 更新库存
            shopDAO.updateProductStock(productId, quantity);
        }

        return success;
    }

    public boolean purchaseCart(int userId, List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }

        // 检查所有商品库存是否足够
        for (CartItem item : items) {
            Product product = shopDAO.getProductById(item.getProductId());
            if (product == null || product.getStock() < item.getQuantity()) {
                return false;
            }
        }

        // 创建订单
        boolean success = shopDAO.createOrderFromCart(userId, items);

        if (success) {
            // 更新所有商品库存
            for (CartItem item : items) {
                shopDAO.updateProductStock(item.getProductId(), item.getQuantity());
            }
        }

        return success;
    }

    public List<Order> getUserOrders(int userId) {
        return shopDAO.getUserOrders(userId);
    }
}