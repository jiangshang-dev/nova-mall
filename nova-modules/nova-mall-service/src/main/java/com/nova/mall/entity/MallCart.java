package com.nova.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mall_cart")
public class MallCart extends BaseModel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer userId;
    private Integer goodsId;
    private Integer quantity;
    private Integer checked;
    @TableLogic
    private Integer deleted;
}
