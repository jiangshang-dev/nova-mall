package com.nova.mall.entity;
import com.baomidou.mybatisplus.annotation.*;
import com.nova.mall.base.BaseModel;
import lombok.Data; import lombok.EqualsAndHashCode;
@Data @EqualsAndHashCode(callSuper = true) @TableName("mall_goods_category")
public class MallGoodsCategory extends BaseModel {
  @TableId(type = IdType.AUTO) private Integer id;
  private Integer parentId; private String name; private Integer sort; private Integer status;
  @TableLogic private Integer deleted;
}
