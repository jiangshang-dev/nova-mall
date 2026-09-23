package com.nova.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {
    private Long id;
    private Integer goodsId;
    private String goodsName;
    private String goodsCover;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal amount;
}
