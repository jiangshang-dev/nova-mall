package com.nova.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mall_goods_comment")
public class MallGoodsComment extends BaseModel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer goodsId;
    private Integer userId;
    private String userName;
    /** 1-5 星 */
    private Integer star;
    private String content;
    private Integer status;
    @TableLogic
    private Integer deleted;
}
