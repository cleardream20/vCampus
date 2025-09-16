// Product.java
package com.seu.vcampus.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    private String imageURL;
    private String location;
}