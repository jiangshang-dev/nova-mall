package com.nova.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mall_order_item")
public class MallOrderItem extends BaseModel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String orderNo;
    private Integer goodsId;
    private String goodsName;
    private String goodsCover;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal amount;
    @TableLogic
    private Integer deleted;
}
