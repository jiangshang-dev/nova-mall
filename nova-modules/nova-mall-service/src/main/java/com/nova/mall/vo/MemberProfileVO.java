package com.nova.mall.vo;

import lombok.Data;

@Data
public class MemberProfileVO {
    private Integer userId;
    private String userName;
    private String userRealName;
    private String avatar;
    private String phone;
    private String email;
}
