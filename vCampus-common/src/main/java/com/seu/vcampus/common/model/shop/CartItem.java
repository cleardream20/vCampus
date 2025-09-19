// CartItem.java
package com.seu.vcampus.common.model.shop;

import com.seu.vcampus.common.util.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable, Jsonable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String userId;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private Date addDate;
}