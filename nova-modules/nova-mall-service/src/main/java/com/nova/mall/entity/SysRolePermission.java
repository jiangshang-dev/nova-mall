package com.nova.mall.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
@Data @TableName("sys_role_permission")
public class SysRolePermission {
  @TableId(type = IdType.AUTO) private Integer id;
  private Integer roleId; private Integer permissionId;
}
