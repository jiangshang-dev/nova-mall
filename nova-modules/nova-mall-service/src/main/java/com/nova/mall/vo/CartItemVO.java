package com.nova.mall.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemVO {
    private Long id;
    private Integer goodsId;
    private Integer quantity;
    private Integer checked;
    private String name;
    private String cover;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
}
