package com.nova.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mall_cs_session")
public class MallCsSession extends BaseModel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String sessionNo;
    private Integer memberId;
    private String memberName;
    private Integer agentId;
    private String agentName;
    /** 0排队 1会话中 2已结束 */
    private Integer status;
    /** 会员客户端 IP */
    private String clientIp;
    private String lastMsg;
    private Long lastTime;
    @TableLogic
    private Integer deleted;
}
