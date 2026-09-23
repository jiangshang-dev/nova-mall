package com.nova.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mall_goods")
public class MallGoods extends BaseModel {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer categoryId;
    private String name;
    private String subTitle;
    /** 商品详情（富文本/HTML） */
    private String detail;
    private String cover;
    /** 优惠价 / 售价 */
    private BigDecimal price;
    /** 原价（划线价） */
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private Integer status;
    @TableLogic
    private Integer deleted;
}
