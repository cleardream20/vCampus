// Order.java
package com.seu.vcampus.common.model.shop;

import com.seu.vcampus.common.util.Jsonable;
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
public class Order implements Serializable, Jsonable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Integer orderId;
    private String userId;
    private Date orderDate;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private String contactPhone;
    private List<OrderItem> items;
}