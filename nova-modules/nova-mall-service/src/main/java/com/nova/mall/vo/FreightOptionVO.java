package com.nova.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FreightOptionVO {
    private String deliveryType;
    private String deliveryName;
    private BigDecimal freight;
    private BigDecimal freeThreshold;
    private BigDecimal payableFreight;
    private Boolean freeShipping;
    private String tip;
}
