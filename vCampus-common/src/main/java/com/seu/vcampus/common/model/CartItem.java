package com.seu.vcampus.common.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {
    private Integer productId;
    private String productName;
    private Double price;
    private Integer quantity;

    public Double getTotalPrice() {
        return price * quantity;
    }
}
