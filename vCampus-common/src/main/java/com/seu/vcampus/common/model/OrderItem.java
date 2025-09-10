package com.seu.vcampus.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Serializable {
    private Integer productId;
    private String productName;
    private Double price;
    private Integer quantity;

    public Double getSubtotal() {
        return price * quantity;
    }
}