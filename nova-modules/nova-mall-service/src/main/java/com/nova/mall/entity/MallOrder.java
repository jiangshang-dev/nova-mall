package com.nova.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mall_order")
public class MallOrder extends BaseModel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Integer userId;
    /** 0待支付 1已支付 2已取消 3已发货 4已完成 */
    private Integer status;
    /** alipay / wxpay */
    private String payType;
    private Long payTime;
    private String payTradeNo;
    private BigDecimal totalAmount;
    private BigDecimal goodsAmount;
    private BigDecimal freightAmount;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    @TableLogic
    private Integer deleted;
}
