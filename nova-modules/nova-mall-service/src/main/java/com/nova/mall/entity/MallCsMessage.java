package com.nova.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mall_cs_message")
public class MallCsMessage extends BaseModel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sessionId;
    private String sessionNo;
    private Integer fromUserId;
    private String fromUserName;
    private String fromRole;
    private String content;
    @TableLogic
    private Integer deleted;
}
