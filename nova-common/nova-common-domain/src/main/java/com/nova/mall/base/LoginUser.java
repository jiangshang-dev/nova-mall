package com.nova.mall.base;

import com.nova.mall.system.enums.UserFlagEnum;
import lombok.Data;
import java.util.Date;
import java.util.List;

/** 登录用户上下文 */
@Data
public class LoginUser {
    private Integer id;
    private String userName;
    private String userRealName;
    private UserFlagEnum userFlag;
    private String token;
    private String host;
    private Date expire;
    private List<String> roles;
    private List<String> permissions;
}
