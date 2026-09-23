package com.nova.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberProfileDTO {
    @NotBlank(message = "请填写昵称")
    @Size(max = 32)
    private String userRealName;

    @Size(max = 512)
    private String avatar;
}
