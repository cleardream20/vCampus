// ShopController.java
package com.seu.vcampus.server.controller;

import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.model.Order;
import com.seu.vcampus.common.model.CartItem;
import com.seu.vcampus.common.model.Product;
import com.seu.vcampus.server.service.ShopService;
import com.seu.vcampus.server.controller.ShopController;

import java.math.BigDecimal;
import java.util.List;
import java.util.List;
import java.util.Map;

public class ShopController {
    private com.seu.vcampus.server.controller.ShopController shopService;

    public ShopController() {
        this.shopService = new com.seu.vcampus.server.controller.ShopController();
    }

    // 商品相关方法
    public List<Product> getAllProducts() {
        return shopService.getAllProducts();
    }

    public Product getProductById(String productId) {
        return shopService.getProductById(productId);
    }

    public List<Product> getProductsByCategory(String category) {
        return shopService.getProductsByCategory(category);
    }

    public boolean addProduct(Product product) {
        return shopService.addProduct(product);
    }

    public boolean updateProduct(Product product) {
        return shopService.updateProduct(product);
    }

    public boolean deleteProduct(String productId) {
        return shopService.deleteProduct(productId);
    }

    // 购物车相关方法
    public List<CartItem> getCartItems(Integer userId) {
        return shopService.getCartItems(userId);
    }

    public boolean addToCart(Integer userId, Integer productId, Integer quantity) {
        Product product = shopService.getProductById(String.valueOf(productId));
        if (product == null) {
            return false;
        }

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);
        cartItem.setProductName(product.getProductName());
        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getPrice());

        return shopService.addToCart(cartItem);
    }

    public boolean updateCartItemQuantity(Integer userId, Integer productId, int quantity) {
        return shopService.updateCartItemQuantity(userId, productId, quantity);
    }

    public boolean removeFromCart(Integer userId, Integer productId) {
        return shopService.removeFromCart(userId, productId);
    }

    public boolean clearCart(Integer userId) {
        return shopService.clearCart(userId);
    }

    public BigDecimal getCartTotal(Integer userId) {
        List<CartItem> cartItems = shopService.getCartItems(userId);
        return shopService.calculateCartTotal(cartItems);
    }

    // 订单相关方法
    public List<Order> getUserOrders(Integer userId) {
        return shopService.getUserOrders(userId);
    }

    public Order getOrderById(Integer orderId) {
        return shopService.getOrderById(orderId);
    }

    public boolean createOrder(Integer userId) {
        return shopService.createOrderFromCart(userId);
    }

    public boolean updateOrderStatus(Integer orderId, String status) {
        return shopService.updateOrderStatus(orderId, status);
    }


    public Message handleRequest(Message request) {
        Message response = new Message();
        response.setType(request.getType() + "_RESPONSE");

        try {
            switch (request.getType()) {
                case "SHOP_GET_PRODUCTS":
                    handleGetProducts(request, response);
                    break;
                case "SHOP_GET_PRODUCTS_BY_CATEGORY":
                    handleGetProductsByCategory(request, response);
                    break;
                case "SHOP_GET_PRODUCT_DETAIL":
                    handleGetProductDetail(request, response);
                    break;
                case "SHOP_ADD_TO_CART":
                    handleAddToCart(request, response);
                    break;
                case "SHOP_GET_CART_ITEMS":
                    handleGetCartItems(request, response);
                    break;
                case "SHOP_UPDATE_CART_ITEM":
                    handleUpdateCartItem(request, response);
                    break;
                case "SHOP_REMOVE_FROM_CART":
                    handleRemoveFromCart(request, response);
                    break;
                case "SHOP_CLEAR_CART":
                    handleClearCart(request, response);
                    break;
                case "SHOP_CREATE_ORDER":
                    handleCreateOrder(request, response);
                    break;
                case "SHOP_GET_ORDERS":
                    handleGetOrders(request, response);
                    break;
                case "SHOP_GET_ORDER_DETAIL":
                    handleGetOrderDetail(request, response);
                    break;
                default:
                    response.setStatus(Message.STATUS_ERROR);
                    response.setData("未知的商店请求类型: " + request.getType());
                    break;
            }
        } catch (Exception e) {
            response.setStatus(Message.STATUS_ERROR);
            response.setData("处理请求时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    private void handleGetProducts(Message request, Message response) {
        List<Product> products = shopService.getAllProducts();
        response.setStatus(Message.STATUS_SUCCESS);
        response.setData(products);
    }

    private void handleGetProductsByCategory(Message request, Message response) {
        String category = (String) request.getData();
        List<Product> products = shopService.getProductsByCategory(category);
        response.setStatus(Message.STATUS_SUCCESS);
        response.setData(products);
    }

    private void handleGetProductDetail(Message request, Message response) {
        String productId = (String) request.getData();
        Product product = shopService.getProductById(productId);

        if (product != null) {
            response.setStatus(Message.STATUS_SUCCESS);
            response.setData(product);
        } else {
            response.setStatus(Message.STATUS_ERROR);
            response.setData("未找到商品: " + productId);
        }
    }

    private void handleAddToCart(Message request, Message response) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) request.getData();
            Integer userId = (Integer) data.get("userId");
            Integer productId = (Integer) data.get("productId");
            Integer quantity = (Integer) data.get("quantity");

            boolean success = shopService.addToCart(userId, productId, quantity);

            if (success) {
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData("商品已添加到购物车");
            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("添加商品到购物车失败");
            }
        } catch (Exception e) {
            response.setStatus(Message.STATUS_ERROR);
            response.setData("参数错误: " + e.getMessage());
        }
    }

    private void handleGetCartItems(Message request, Message response) {
        Integer userId = (Integer) request.getData();
        List<CartItem> cartItems = shopService.getCartItems(userId);
        response.setStatus(Message.STATUS_SUCCESS);
        response.setData(cartItems);
    }

    private void handleUpdateCartItem(Message request, Message response) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) request.getData();
            Integer userId = (Integer) data.get("userId");
            Integer productId = (Integer) data.get("productId");
            Integer quantity = (Integer) data.get("quantity");

            boolean success = shopService.updateCartItemQuantity(userId, productId, quantity);

            if (success) {
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData("购物车商品数量已更新");
            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("更新购物车商品数量失败");
            }
        } catch (Exception e) {
            response.setStatus(Message.STATUS_ERROR);
            response.setData("参数错误: " + e.getMessage());
        }
    }

    private void handleRemoveFromCart(Message request, Message response) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) request.getData();
            Integer userId = (Integer) data.get("userId");
            Integer productId = (Integer) data.get("productId");

            boolean success = shopService.removeFromCart(userId, productId);

            if (success) {
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData("商品已从购物车移除");
            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("从购物车移除商品失败");
            }
        } catch (Exception e) {
            response.setStatus(Message.STATUS_ERROR);
            response.setData("参数错误: " + e.getMessage());
        }
    }

    private void handleClearCart(Message request, Message response) {
        Integer userId = (Integer) request.getData();
        boolean success = shopService.clearCart(userId);

        if (success) {
            response.setStatus(Message.STATUS_SUCCESS);
            response.setData("购物车已清空");
        } else {
            response.setStatus(Message.STATUS_ERROR);
            response.setData("清空购物车失败");
        }
    }

    private void handleCreateOrder(Message request, Message response) {
        Integer userId = (Integer) request.getData();
        boolean success = shopService.createOrder(userId);

        if (success) {
            response.setStatus(Message.STATUS_SUCCESS);
            response.setData("订单创建成功");
        } else {
            response.setStatus(Message.STATUS_ERROR);
            response.setData("订单创建失败，请检查库存或购物车");
        }
    }

    private void handleGetOrders(Message request, Message response) {
        Integer userId = (Integer) request.getData();
        List<com.seu.vcampus.common.model.Order> orders = shopService.getUserOrders(userId);
        response.setStatus(Message.STATUS_SUCCESS);
        response.setData(orders);
    }

    private void handleGetOrderDetail(Message request, Message response) {
        Integer orderId = (Integer) request.getData();
        com.seu.vcampus.common.model.Order order = shopService.getOrderById(orderId);

        if (order != null) {
            response.setStatus(Message.STATUS_SUCCESS);
            response.setData(order);
        } else {
            response.setStatus(Message.STATUS_ERROR);
            response.setData("未找到订单: " + orderId);
        }
    }
}