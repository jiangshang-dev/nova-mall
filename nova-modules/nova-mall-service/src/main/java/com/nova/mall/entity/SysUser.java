package com.nova.mall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import com.nova.mall.system.enums.UserFlagEnum;
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
    /** 1内部 2管理员 3普通用户，MyBatis-Plus IEnum 自动映射 */
    private UserFlagEnum userFlag;
    private Integer status;
    @TableLogic
    private Integer deleted;
    private String remark;
}
