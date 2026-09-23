package com.nova.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseModel {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String userName;
    private String password;
    private String userRealName;
    private String phone;
    private String email;
    private String avatar;
    private Integer userFlag;
    private Integer status;
    @TableLogic
    private Integer deleted;
    private String remark;
}
