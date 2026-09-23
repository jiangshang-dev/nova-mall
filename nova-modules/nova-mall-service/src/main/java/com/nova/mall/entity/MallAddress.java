package com.nova.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mall_address")
public class MallAddress extends BaseModel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer userId;
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;
    @TableLogic
    private Integer deleted;
}
