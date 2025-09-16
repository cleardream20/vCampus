// ShopController.java
package com.seu.vcampus.server.controller;

import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.model.Order;
import com.seu.vcampus.common.model.CartItem;
import com.seu.vcampus.common.model.Product;
import com.seu.vcampus.server.service.ShopService;
import com.seu.vcampus.server.controller.ShopController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.List;
import java.util.Map;

public class ShopController {
    private ShopService shopService;

    public ShopController() {
        this.shopService = new ShopService();
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

    public boolean addToCart(Integer userId, String productId, Integer quantity) {
        Product product = shopService.getProductById(productId);
        if (product == null) {
            return false;
        }

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setProductId(productId);
        cartItem.setProductName(product.getProductName());
        cartItem.setQuantity(quantity);
        cartItem.setPrice(product.getPrice());

        return shopService.addToCart(userId, productId, quantity);
    }

    public boolean updateCartItemQuantity(Integer userId, String productId, int quantity) {
        return shopService.updateCartItemQuantity(userId, productId, quantity);
    }

    public boolean removeFromCart(Integer userId, String productId) {
        return shopService.removeFromCart(userId, productId);
    }

    public CartItem getCartItem(Integer userId, String productId) {
        return shopService.getCartItem(userId, productId);
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
        System.out.println("ShopController 收到子类型: " + request.getType());
        Message response = new Message();
        response.setType(request.getType() + "_RESPONSE");

        try {
            System.out.println("处理商店请求: " + request.getType());

            // 根据请求类型路由到相应的处理方法
            switch (request.getType()) {
                case "SHOP_GET_PRODUCTS":
                    response = handleGetProducts(request, response);
                    break;
                case "SHOP_GET_PRODUCTS_BY_CATEGORY":
                    response = handleGetProductsByCategory(request, response);
                    break;
                case "SHOP_GET_PRODUCT_DETAIL":
                    response = handleGetProductDetail(request, response);
                    break;
                case "SHOP_ADD_TO_CART":
                    response = handleAddToCart(request, response);
                    break;
                case "SHOP_GET_CART_ITEMS":
                    response = handleGetCartItems(request, response);
                    break;
                case "SHOP_UPDATE_CART_ITEM":
                    response = handleUpdateCartItem(request, response);
                    break;
                case "SHOP_REMOVE_FROM_CART":
                    response = handleRemoveFromCart(request, response);
                    break;
                case "SHOP_CLEAR_CART":
                    response = handleClearCart(request, response);
                    break;
                case "SHOP_CREATE_ORDER":
                    response = handleCreateOrder(request, response);
                    break;
                case "SHOP_GET_ORDERS":
                    response = handleGetOrders(request, response);
                    break;
                case "SHOP_GET_ORDER_DETAIL":
                    response = handleGetOrderDetail(request, response);
                    break;
                default:
                    response.setStatus(Message.STATUS_ERROR);
                    response.setData("未知的商店请求类型: " + request.getType());
                    System.err.println("未知的商店请求类型: " + request.getType());
                    break;
            }
        } catch (Exception e) {
            System.err.println("处理商店请求时发生未捕获的异常: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(Message.STATUS_ERROR);
            response.setData("处理请求时发生未预期的错误: " + e.getMessage());
        }

        return response;
    }

    private Message handleGetProducts(Message request, Message response) {
        try {
            System.out.println("获取商品列表...");
            List<Product> products = shopService.getAllProducts();

            if (products != null && !products.isEmpty()) {
                System.out.println("找到 " + products.size() + " 个商品");
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData(products);
            } else {
                System.out.println("商品列表为空");
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData(new ArrayList<Product>());
            }
        } catch (Exception e) {
            System.err.println("获取商品列表时发生错误: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(Message.STATUS_ERROR);
            response.setData("获取商品列表失败: " + e.getMessage());
        }

        return response;
    }

    private Message handleGetProductsByCategory(Message request, Message response) {
        try {
            String category = (String) request.getData();
            System.out.println("按分类获取商品: " + category);

            List<Product> products = shopService.getProductsByCategory(category);

            if (products != null && !products.isEmpty()) {
                System.out.println("找到 " + products.size() + " 个商品");
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData(products);
            } else {
                System.out.println("该分类下没有商品");
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData(new ArrayList<Product>());
            }
        } catch (Exception e) {
            System.err.println("按分类获取商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(Message.STATUS_ERROR);
            response.setData("按分类获取商品失败: " + e.getMessage());
        }

        return response;
    }

    private Message handleGetProductDetail(Message request, Message response) {
        try {
            String productId = (String) request.getData();
            System.out.println("获取商品详情: " + productId);

            Product product = shopService.getProductById(productId);

            if (product != null) {
                System.out.println("找到商品: " + product.getProductName());
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData(product);
            } else {
                System.out.println("未找到商品: " + productId);
                response.setStatus(Message.STATUS_ERROR);
                response.setData("未找到商品: " + productId);
            }
        } catch (Exception e) {
            System.err.println("获取商品详情时发生错误: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(Message.STATUS_ERROR);
            response.setData("获取商品详情失败: " + e.getMessage());
        }

        return response;
    }

    private Message handleAddToCart(Message request, Message response) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) request.getData();
            Integer userId = (Integer) data.get("userId");
            String productId = (String) data.get("productId");
            Integer quantity = (Integer) data.get("quantity");

            System.out.println("添加到购物车 - 用户ID: " + userId + ", 商品ID: " + productId + ", 数量: " + quantity);

            boolean success = shopService.addToCart(userId, productId, quantity);

            if (success) {
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData("商品已添加到购物车");
            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("添加商品到购物车失败");
            }
        } catch (Exception e) {
            System.err.println("添加到购物车时发生错误: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(Message.STATUS_ERROR);
            response.setData("添加到购物车失败: " + e.getMessage());
        }

        return response;
    }

    private Message handleGetCartItems(Message request, Message response) {
        try {
            Integer userId = (Integer) request.getData();
            System.out.println("获取购物车商品 - 用户ID: " + userId);

            List<CartItem> cartItems = shopService.getCartItems(userId);

            if (cartItems != null) {
                System.out.println("找到 " + cartItems.size() + " 个购物车商品");
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData(cartItems);
            } else {
                System.out.println("购物车为空");
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData(new ArrayList<CartItem>());
            }
        } catch (Exception e) {
            System.err.println("获取购物车商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(Message.STATUS_ERROR);
            response.setData("获取购物车商品失败: " + e.getMessage());
        }

        return response;
    }

    private Message handleUpdateCartItem(Message request, Message response) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) request.getData();
            Integer userId = (Integer) data.get("userId");
            String productId = (String) data.get("productId");
            Integer quantity = (Integer) data.get("quantity");

            System.out.println("更新购物车商品 - 用户ID: " + userId + ", 商品ID: " + productId + ", 数量: " + quantity);

            boolean success = shopService.updateCartItemQuantity(userId, productId, quantity);

            if (success) {
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData("购物车商品数量已更新");
            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("更新购物车商品数量失败");
            }
        } catch (Exception e) {
            System.err.println("更新购物车商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(Message.STATUS_ERROR);
            response.setData("更新购物车商品失败: " + e.getMessage());
        }

        return response;
    }

    private Message handleRemoveFromCart(Message request, Message response) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) request.getData();
            Integer userId = (Integer) data.get("userId");
            String productId = (String) data.get("productId");

            System.out.println("从购物车移除 - 用户ID: " + userId + ", 商品ID: " + productId);

            boolean success = shopService.removeFromCart(userId, productId);

            if (success) {
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData("商品已从购物车移除");
            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("从购物车移除商品失败");
            }
        } catch (Exception e) {
            System.err.println("从购物车移除商品时发生错误: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(Message.STATUS_ERROR);
            response.setData("从购物车移除商品失败: " + e.getMessage());
        }

        return response;
    }

    private Message handleClearCart(Message request, Message response) {
        try {
            Integer userId = (Integer) request.getData();
            System.out.println("清空购物车 - 用户ID: " + userId);

            boolean success = shopService.clearCart(userId);

            if (success) {
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData("购物车已清空");
            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("清空购物车失败");
            }
        } catch (Exception e) {
            System.err.println("清空购物车时发生错误: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(Message.STATUS_ERROR);
            response.setData("清空购物车失败: " + e.getMessage());
        }

        return response;
    }

    private Message handleCreateOrder(Message request, Message response) {
        try {
            Integer userId = (Integer) request.getData();
            System.out.println("创建订单 - 用户ID: " + userId);

            boolean success = shopService.createOrderFromCart(userId);

            if (success) {
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData("订单创建成功");
            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("订单创建失败，请检查库存或购物车");
            }
        } catch (Exception e) {
            System.err.println("创建订单时发生错误: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(Message.STATUS_ERROR);
            response.setData("创建订单失败: " + e.getMessage());
        }

        return response;
    }

    private Message handleGetOrders(Message request, Message response) {
        try {
            Integer userId = (Integer) request.getData();
            System.out.println("获取订单 - 用户ID: " + userId);

            List<Order> orders = shopService.getUserOrders(userId);

            if (orders != null) {
                System.out.println("找到 " + orders.size() + " 个订单");
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData(orders);
            } else {
                System.out.println("没有找到订单");
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData(new ArrayList<Order>());
            }
        } catch (Exception e) {
            System.err.println("获取订单时发生错误: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(Message.STATUS_ERROR);
            response.setData("获取订单失败: " + e.getMessage());
        }

        return response;
    }

    private Message handleGetOrderDetail(Message request, Message response) {
        try {
            Integer orderId = (Integer) request.getData();
            System.out.println("获取订单详情 - 订单ID: " + orderId);

            Order order = shopService.getOrderById(orderId);

            if (order != null) {
                System.out.println("找到订单: " + orderId);
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData(order);
            } else {
                System.out.println("未找到订单: " + orderId);
                response.setStatus(Message.STATUS_ERROR);
                response.setData("未找到订单: " + orderId);
            }
        } catch (Exception e) {
            System.err.println("获取订单详情时发生错误: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(Message.STATUS_ERROR);
            response.setData("获取订单详情失败: " + e.getMessage());
        }

        return response;
    }
}