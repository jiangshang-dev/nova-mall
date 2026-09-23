package com.nova.mall.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("mall_shop_config")
public class MallShopConfig {
    @TableId
    private String configKey;
    private String configValue;
    private String remark;
    private Long updateTime;
}
