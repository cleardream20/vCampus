// Order.java
package com.seu.vcampus.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {
    private int id;
    private String orderId;
    private String userId;
    private Date orderDate;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private String contactPhone;
    private List<OrderItem> items;
}