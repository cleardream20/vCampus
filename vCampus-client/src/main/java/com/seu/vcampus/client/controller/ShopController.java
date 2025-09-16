package com.seu.vcampus.client.controller;

import com.seu.vcampus.client.socket.ClientSocketHandler;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.model.Product;
import com.seu.vcampus.common.model.CartItem;
import com.seu.vcampus.common.model.Order;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ShopController {
    private ClientSocketHandler socketHandler;

    public ShopController() {
        this.socketHandler = new ClientSocketHandler();
    }

    // 添加一个方法来检查并建立连接
    private boolean ensureConnected() {
        if (!socketHandler.isConnected()) {
            return socketHandler.connect();
        }
        return true;
    }

    public List<Product> getAllProducts() {
        if (!ensureConnected()) {
            throw new RuntimeException("无法连接到服务器");
        }

        // 创建请求消息
        Message request = new Message();
        request.setType("SHOP_GET_PRODUCTS");

        // 发送请求并获取响应
        Message response = socketHandler.sendRequest(request);

        if (response.getStatus() == Message.STATUS_SUCCESS) {
            @SuppressWarnings("unchecked")
            List<Product> products = (List<Product>) response.getData();
            return products;
        } else {
            throw new RuntimeException("获取商品列表失败: " + response.getData());
        }
    }

    public List<Product> getProductsByCategory(String category) {
        if (!ensureConnected()) {
            throw new RuntimeException("无法连接到服务器");
        }

        // 创建请求消息
        Message request = new Message();
        request.setType("SHOP_GET_PRODUCTS_BY_CATEGORY");
        request.setData(category);

        // 发送请求并获取响应
        Message response = socketHandler.sendRequest(request);

        if (response.getStatus() == Message.STATUS_SUCCESS) {
            @SuppressWarnings("unchecked")
            List<Product> products = (List<Product>) response.getData();
            return products;
        } else {
            throw new RuntimeException("按分类获取商品失败: " + response.getData());
        }
    }

    public Product getProductById(String productId) {
        if (!ensureConnected()) {
            throw new RuntimeException("无法连接到服务器");
        }

        // 创建请求消息
        Message request = new Message();
        request.setType("SHOP_GET_PRODUCT_DETAIL");
        request.setData(productId);

        // 发送请求并获取响应
        Message response = socketHandler.sendRequest(request);

        if (response.getStatus() == Message.STATUS_SUCCESS) {
            return (Product) response.getData();
        } else {
            throw new RuntimeException("获取商品详情失败: " + response.getData());
        }
    }

    public boolean addToCart(int userId, int productId, int quantity) {
        if (!ensureConnected()) {
            throw new RuntimeException("无法连接到服务器");
        }

        // 创建请求数据
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("productId", productId);
        data.put("quantity", quantity);

        // 创建请求消息
        Message request = new Message();
        request.setType("SHOP_ADD_TO_CART");
        request.setData(data);

        // 发送请求并获取响应
        Message response = socketHandler.sendRequest(request);

        if (response.getStatus() == Message.STATUS_SUCCESS) {
            return true;
        } else {
            throw new RuntimeException("添加到购物车失败: " + response.getData());
        }
    }

    public List<CartItem> getCartItems(int userId) {
        if (!ensureConnected()) {
            throw new RuntimeException("无法连接到服务器");
        }

        // 创建请求消息
        Message request = new Message();
        request.setType("SHOP_GET_CART_ITEMS");
        request.setData(userId);

        // 发送请求并获取响应
        Message response = socketHandler.sendRequest(request);

        if (response.getStatus() == Message.STATUS_SUCCESS) {
            @SuppressWarnings("unchecked")
            List<CartItem> cartItems = (List<CartItem>) response.getData();
            return cartItems;
        } else {
            throw new RuntimeException("获取购物车失败: " + response.getData());
        }
    }

    public boolean updateCartItemQuantity(int userId, int productId, int quantity) {
        if (!ensureConnected()) {
            throw new RuntimeException("无法连接到服务器");
        }

        // 创建请求数据
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("productId", productId);
        data.put("quantity", quantity);

        // 创建请求消息
        Message request = new Message();
        request.setType("SHOP_UPDATE_CART_ITEM");
        request.setData(data);

        // 发送请求并获取响应
        Message response = socketHandler.sendRequest(request);

        if (response.getStatus() == Message.STATUS_SUCCESS) {
            return true;
        } else {
            throw new RuntimeException("更新购物车失败: " + response.getData());
        }
    }

    public boolean removeFromCart(int userId, int productId) {
        if (!ensureConnected()) {
            throw new RuntimeException("无法连接到服务器");
        }

        // 创建请求数据
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("productId", productId);

        // 创建请求消息
        Message request = new Message();
        request.setType("SHOP_REMOVE_FROM_CART");
        request.setData(data);

        // 发送请求并获取响应
        Message response = socketHandler.sendRequest(request);

        if (response.getStatus() == Message.STATUS_SUCCESS) {
            return true;
        } else {
            throw new RuntimeException("从购物车移除失败: " + response.getData());
        }
    }

    public boolean clearCart(int userId) {
        if (!ensureConnected()) {
            throw new RuntimeException("无法连接到服务器");
        }

        // 创建请求消息
        Message request = new Message();
        request.setType("SHOP_CLEAR_CART");
        request.setData(userId);

        // 发送请求并获取响应
        Message response = socketHandler.sendRequest(request);

        if (response.getStatus() == Message.STATUS_SUCCESS) {
            return true;
        } else {
            throw new RuntimeException("清空购物车失败: " + response.getData());
        }
    }

    public boolean createOrder(int userId) {
        if (!ensureConnected()) {
            throw new RuntimeException("无法连接到服务器");
        }

        // 创建请求消息
        Message request = new Message();
        request.setType("SHOP_CREATE_ORDER");
        request.setData(userId);

        // 发送请求并获取响应
        Message response = socketHandler.sendRequest(request);

        if (response.getStatus() == Message.STATUS_SUCCESS) {
            return true;
        } else {
            throw new RuntimeException("创建订单失败: " + response.getData());
        }
    }

    public List<Order> getUserOrders(int userId) {
        if (!ensureConnected()) {
            throw new RuntimeException("无法连接到服务器");
        }

        // 创建请求消息
        Message request = new Message();
        request.setType("SHOP_GET_ORDERS");
        request.setData(userId);

        // 发送请求并获取响应
        Message response = socketHandler.sendRequest(request);

        if (response.getStatus() == Message.STATUS_SUCCESS) {
            @SuppressWarnings("unchecked")
            List<Order> orders = (List<Order>) response.getData();
            return orders;
        } else {
            throw new RuntimeException("获取订单失败: " + response.getData());
        }
    }

    public Order getOrderById(int orderId) {
        if (!ensureConnected()) {
            throw new RuntimeException("无法连接到服务器");
        }

        // 创建请求消息
        Message request = new Message();
        request.setType("SHOP_GET_ORDER_DETAIL");
        request.setData(orderId);

        // 发送请求并获取响应
        Message response = socketHandler.sendRequest(request);

        if (response.getStatus() == Message.STATUS_SUCCESS) {
            return (Order) response.getData();
        } else {
            throw new RuntimeException("获取订单详情失败: " + response.getData());
        }
    }

    public double getCartTotal(int userId) {
        List<CartItem> cartItems = getCartItems(userId);
        double total = 0;

        for (CartItem item : cartItems) {
            total += item.getPrice().doubleValue() * item.getQuantity();
        }

        return total;
    }

    // 关闭 socket 连接
    public void close() {
        socketHandler.close();
    }
}