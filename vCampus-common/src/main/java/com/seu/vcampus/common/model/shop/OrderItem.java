// OrderItem.java
package com.seu.vcampus.common.model.shop;

import com.seu.vcampus.common.util.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Serializable, Jsonable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer orderId;
    private String productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}