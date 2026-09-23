package com.nova.mall.entity;
import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import lombok.Data; import lombok.EqualsAndHashCode;
@Data @EqualsAndHashCode(callSuper = true) @TableName("sys_role")
public class SysRole extends BaseModel {
  @TableId(type = IdType.AUTO) private Integer id;
  private String roleCode; private String roleName; private Integer sort;
  private Integer status; @TableLogic private Integer deleted; private String remark;
}
