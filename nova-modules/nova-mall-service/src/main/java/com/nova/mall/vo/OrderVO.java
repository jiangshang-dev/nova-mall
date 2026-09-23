package com.nova.mall.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Integer userId;
    private Integer status;
    private String statusText;
    private String payType;
    private String payTypeText;
    private Long payTime;
    private String payTradeNo;
    private BigDecimal totalAmount;
    private BigDecimal goodsAmount;
    private BigDecimal freightAmount;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private Long createTime;
    private List<OrderItemVO> items;
}
