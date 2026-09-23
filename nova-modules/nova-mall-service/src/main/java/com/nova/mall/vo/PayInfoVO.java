package com.nova.mall.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayInfoVO {
    private String orderNo;
    private String payType;
    private String payTypeText;
    private BigDecimal amount;
    /** 演示模式：true 表示未接真实商户，走模拟支付 */
    private Boolean demoMode;
    private String payTip;
    private String mockPayUrl;
}
