package com.nova.mall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShippingOptionsVO {
    private String serviceCity;
    private String serviceTip;
    private List<FreightOptionVO> options;
    private BigDecimal goodsAmount;
}
