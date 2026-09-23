package com.nova.mall.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
@Data @TableName("sys_user_role")
public class SysUserRole {
  @TableId(type = IdType.AUTO) private Integer id;
  private Integer userId; private Integer roleId;
}
