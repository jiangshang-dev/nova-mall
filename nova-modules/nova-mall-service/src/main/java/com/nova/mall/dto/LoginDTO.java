package com.nova.mall.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data @Schema(description="登录请求")
public class LoginDTO {
  @NotBlank(message="用户名不能为空") private String username;
  @NotBlank(message="密码不能为空") private String password;
  private String clientType;
}
