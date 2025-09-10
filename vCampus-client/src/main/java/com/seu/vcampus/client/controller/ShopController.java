package com.seu.vcampus.client.controller;

import com.seu.vcampus.client.socket.Client;
import com.seu.vcampus.common.Message;
import com.seu.vcampus.common.model.Product;
import com.seu.vcampus.common.model.CartItem;
import com.seu.vcampus.common.model.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ShopController {
    private Client client;
    private Gson gson;
    private Map<Integer, CartItem> cart; // 本地购物车

    public ShopController() {
        this.client = Client.getInstance();
        this.gson = new Gson();
        this.cart = new HashMap<>();
    }

    public List<Product> getAvailableProducts() {
        Message request = new Message("SHOP", "GET_PRODUCTS", null);
        Message response = client.sendRequest(request);

        if ("SUCCESS".equals(response.getStatus())) {
            return gson.fromJson(response.getData(),
                    new TypeToken<List<Product>>(){}.getType());
        }

        return null;
    }

    public Product getProductById(int productId) {
        Message request = new Message("SHOP", "GET_PRODUCT_BY_ID", String.valueOf(productId));
        Message response = client.sendRequest(request);

        if ("SUCCESS".equals(response.getStatus())) {
            return gson.fromJson(response.getData(), Product.class);
        }

        return null;
    }

    public boolean addToCart(int productId, int quantity) {
        // 先获取商品信息
        Product product = getProductById(productId);
        if (product == null || product.getStock() < quantity) {
            return false;
        }

        // 添加到本地购物车
        CartItem item = cart.get(productId);
        if (item != null) {
            // 如果已存在，增加数量
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // 如果不存在，创建新项
            item = new CartItem(productId, product.getProductName(),
                    product.getPrice(), quantity);
            cart.put(productId, item);
        }

        return true;
    }

    public boolean removeFromCart(int productId) {
        return cart.remove(productId) != null;
    }

    public List<CartItem> getCartItems() {
        return List.copyOf(cart.values());
    }

    public boolean purchaseProduct(int userId, int productId, int quantity) {
        Map<String, Object> purchaseData = new HashMap<>();
        purchaseData.put("userId", userId);
        purchaseData.put("productId", productId);
        purchaseData.put("quantity", quantity);

        Message request = new Message("SHOP", "PURCHASE_PRODUCT", gson.toJson(purchaseData));
        Message response = client.sendRequest(request);

        if ("SUCCESS".equals(response.getStatus())) {
            // 更新本地购物车
            CartItem item = cart.get(productId);
            if (item != null) {
                if (item.getQuantity() <= quantity) {
                    cart.remove(productId);
                } else {
                    item.setQuantity(item.getQuantity() - quantity);
                }
            }
            return true;
        }

        return false;
    }

    public boolean purchaseAllInCart(int userId) {
        if (cart.isEmpty()) {
            return false;
        }

        Map<String, Object> purchaseData = new HashMap<>();
        purchaseData.put("userId", userId);
        purchaseData.put("items", cart.values());

        Message request = new Message("SHOP", "PURCHASE_CART", gson.toJson(purchaseData));
        Message response = client.sendRequest(request);

        if ("SUCCESS".equals(response.getStatus())) {
            cart.clear(); // 清空购物车
            return true;
        }

        return false;
    }

    public List<Order> getUserOrders(int userId) {
        Message request = new Message("SHOP", "GET_ORDERS", String.valueOf(userId));
        Message response = client.sendRequest(request);

        if ("SUCCESS".equals(response.getStatus())) {
            return gson.fromJson(response.getData(),
                    new TypeToken<List<Order>>(){}.getType());
        }

        return null;
    }

    public void clearCart() {
        cart.clear();
    }

    public double getCartTotal() {
        double total = 0;
        for (CartItem item : cart.values()) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public int getCartItemCount() {
        int count = 0;
        for (CartItem item : cart.values()) {
            count += item.getQuantity();
        }
        return count;
    }
}