package com.seu.vcampus.client.controller;

import com.google.gson.reflect.TypeToken;
import com.seu.vcampus.client.socket.ClientSocketUtil;
import com.seu.vcampus.common.util.Jsonable;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.model.shop.Product;
import com.seu.vcampus.common.model.shop.CartItem;
import com.seu.vcampus.common.model.shop.Order;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopController {

    public ShopController() {
        // 空构造函数，使用静态的ClientSocketUtil
    }

    public List<Product> getAllProducts() {
        Message request = new Message();
        request.setType("SHOP_GET_PRODUCTS");

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return Jsonable.fromJson(Jsonable.toJson(response.getData()), new TypeToken<List<Product>>() {}.getType());
            } else {
                System.err.println("获取所有商品失败: " + response.getData());
                return Collections.emptyList();
            }
        } catch (IOException e) {
            System.err.println("获取所有商品请求异常: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public List<Product> getProductsByCategory(String category) {
        Message request = new Message();
        request.setType("SHOP_GET_PRODUCTS_BY_CATEGORY");
        request.setData(category);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return Jsonable.fromJson(Jsonable.toJson(response.getData()), new TypeToken<List<Product>>() {}.getType());
            } else {
                System.err.println("按分类获取商品失败: " + response.getData());
                return Collections.emptyList();
            }
        } catch (IOException e) {
            System.err.println("按分类获取商品请求异常: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public Product getProductById(String productId) {
        Message request = new Message();
        request.setType("SHOP_GET_PRODUCT_DETAIL");
        request.setData(productId);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return (Product) response.getData();
            } else {
                System.err.println("获取商品详情失败: " + response.getData());
                return null;
            }
        } catch (IOException e) {
            System.err.println("获取商品详情请求异常: " + e.getMessage());
        }
        return null;
    }

    public boolean addToCart(String userId, String productId, int quantity) {
        Map<String, Object> cartRequest = new HashMap<>();
        cartRequest.put("userId", userId);
        cartRequest.put("productId", productId);
        cartRequest.put("quantity", quantity);

        Message request = new Message();
        request.setType("SHOP_ADD_TO_CART");
        request.setData(cartRequest);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("添加到购物车失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("添加到购物车请求异常: " + e.getMessage());
        }
        return false;
    }

    public List<CartItem> getCartItems(String userId) {
        Message request = new Message();
        request.setType("SHOP_GET_CART_ITEMS");
        request.setData(userId);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return Jsonable.fromJson(Jsonable.toJson(response.getData()), new TypeToken<List<CartItem>>() {}.getType());
            } else {
                System.err.println("获取购物车失败: " + response.getData());
                return Collections.emptyList();
            }
        } catch (IOException e) {
            System.err.println("获取购物车请求异常: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public boolean updateCartItemQuantity(String userId, String productId, int quantity) {
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("userId", userId);
        updateRequest.put("productId", productId);
        updateRequest.put("quantity", quantity);

        Message request = new Message();
        request.setType("SHOP_UPDATE_CART_ITEM");
        request.setData(updateRequest);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("更新购物车商品数量失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("更新购物车商品数量请求异常: " + e.getMessage());
        }
        return false;
    }

    public boolean removeFromCart(String userId, String productId) {
        Map<String, Object> removeRequest = new HashMap<>();
        removeRequest.put("userId", userId);
        removeRequest.put("productId", productId);

        Message request = new Message();
        request.setType("SHOP_REMOVE_FROM_CART");
        request.setData(removeRequest);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("从购物车移除商品失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("从购物车移除商品请求异常: " + e.getMessage());
        }
        return false;
    }

    public boolean clearCart(String userId) {
        Message request = new Message();
        request.setType("SHOP_CLEAR_CART");
        request.setData(userId);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("清空购物车失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("清空购物车请求异常: " + e.getMessage());
        }
        return false;
    }

    public boolean createOrder(String userId, String shippingAddress, String contactPhone) {
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("userId", userId);
        orderRequest.put("shippingAddress", shippingAddress);
        orderRequest.put("contactPhone", contactPhone);

        Message request = new Message();
        request.setType("SHOP_CREATE_ORDER");
        request.setData(orderRequest);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return true;
            } else {
                System.err.println("创建订单失败: " + response.getData());
                return false;
            }
        } catch (IOException e) {
            System.err.println("创建订单请求异常: " + e.getMessage());
        }
        return false;
    }

    public List<Order> getUserOrders(String userId) {
        Message request = new Message();
        request.setType("SHOP_GET_ORDERS");
        request.setData(userId);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return Jsonable.fromJson(Jsonable.toJson(response.getData()), new TypeToken<List<Order>>() {}.getType());
            } else {
                System.err.println("获取用户订单失败: " + response.getData());
                return Collections.emptyList();
            }
        } catch (IOException e) {
            System.err.println("获取用户订单请求异常: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public Order getOrderById(int orderId) {
        Message request = new Message();
        request.setType("SHOP_GET_ORDER_DETAIL");
        request.setData(orderId);

        try {
            Message response = ClientSocketUtil.sendRequest(request);

            if (response.getStatus().equals(Message.STATUS_SUCCESS)) {
                return (Order) response.getData();
            } else {
                System.err.println("获取订单详情失败: " + response.getData());
                return null;
            }
        } catch (IOException e) {
            System.err.println("获取订单详情请求异常: " + e.getMessage());
        }
        return null;
    }

    public BigDecimal getCartTotal(String userId) {
        List<CartItem> cartItems = getCartItems(userId);
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }

        return total;
    }
}