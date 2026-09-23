package com.nova.mall.vo;
import com.nova.mall.entity.SysPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;
@Data @Schema(description="登录响应")
public class LoginVO {
  private String token; private Integer userId; private String userName; private String userRealName;
  private List<String> roles; private List<String> permissions; private List<SysPermission> menus;
}
