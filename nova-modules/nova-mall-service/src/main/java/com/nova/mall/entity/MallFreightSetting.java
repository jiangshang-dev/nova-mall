package com.nova.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mall_freight_setting")
public class MallFreightSetting extends BaseModel {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String deliveryType;
    private String deliveryName;
    private BigDecimal freight;
    private BigDecimal freeThreshold;
    private Integer enabled;
    private Integer sort;
    private String remark;
    @TableLogic
    private Integer deleted;
}
