// CartItem.java
package com.seu.vcampus.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private Date addDate;
}
