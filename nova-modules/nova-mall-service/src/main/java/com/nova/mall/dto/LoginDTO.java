package com.nova.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "登录/注册请求")
public class LoginDTO {
    @NotBlank(message = "账号不能为空")
    @Schema(description = "账号：用户名 / 邮箱 / 手机号")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @Schema(description = "邮箱（注册时建议填写）")
    private String email;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "客户端：admin / app")
    private String clientType;
}
