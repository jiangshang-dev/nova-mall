package com.nova.mall.entity;
import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import lombok.Data; import lombok.EqualsAndHashCode;
@Data @EqualsAndHashCode(callSuper = true) @TableName("sys_permission")
public class SysPermission extends BaseModel {
  @TableId(type = IdType.AUTO) private Integer id;
  private Integer parentId; private String name; private Integer type;
  private String path; private String component; private String perms; private String icon;
  private Integer sort; private Integer visible; private Integer status;
  @TableLogic private Integer deleted;
}
