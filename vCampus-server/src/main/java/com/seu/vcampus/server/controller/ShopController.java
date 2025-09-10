package com.seu.vcampus.server.controller;

import com.seu.vcampus.common.Message;
import com.seu.vcampus.server.service.ShopService;
import com.seu.vcampus.common.model.Product;
import com.seu.vcampus.common.model.CartItem;
import com.seu.vcampus.common.model.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public class ShopController {
    private ShopService shopService;
    private Gson gson;

    public ShopController() {
        this.shopService = new ShopService();
        this.gson = new Gson();
    }

    public Message handleRequest(Message request) {
        String operation = request.getOperation();
        String data = request.getData();

        try {
            switch (operation) {
                case "GET_PRODUCTS":
                    List<Product> products = shopService.getAvailableProducts();
                    return new Message("SHOP", "SUCCESS", gson.toJson(products));

                case "GET_PRODUCT_BY_ID":
                    int productId = Integer.parseInt(data);
                    Product product = shopService.getProductById(productId);
                    return new Message("SHOP", "SUCCESS", gson.toJson(product));

                case "PURCHASE_PRODUCT":
                    Map<String, Object> purchaseData = gson.fromJson(data,
                            new TypeToken<Map<String, Object>>(){}.getType());
                    int userId = ((Double) purchaseData.get("userId")).intValue();
                    int prodId = ((Double) purchaseData.get("productId")).intValue();
                    int quantity = ((Double) purchaseData.get("quantity")).intValue();

                    boolean success = shopService.purchaseProduct(userId, prodId, quantity);
                    return success ?
                            new Message("SHOP", "SUCCESS", "购买成功") :
                            new Message("SHOP", "FAILED", "购买失败");

                case "PURCHASE_CART":
                    Map<String, Object> cartData = gson.fromJson(data,
                            new TypeToken<Map<String, Object>>(){}.getType());
                    int user = ((Double) cartData.get("userId")).intValue();
                    List<CartItem> items = gson.fromJson(gson.toJson(cartData.get("items")),
                            new TypeToken<List<CartItem>>(){}.getType());

                    boolean cartSuccess = shopService.purchaseCart(user, items);
                    return cartSuccess ?
                            new Message("SHOP", "SUCCESS", "购买成功") :
                            new Message("SHOP", "FAILED", "购买失败");

                case "GET_ORDERS":
                    int uId = Integer.parseInt(data);
                    List<Order> orders = shopService.getUserOrders(uId);
                    return new Message("SHOP", "SUCCESS", gson.toJson(orders));

                default:
                    return new Message("SHOP", "FAILED", "未知操作");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("SHOP", "ERROR", e.getMessage());
        }
    }
}