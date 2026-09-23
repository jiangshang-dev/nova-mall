package com.nova.mall.vo;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
@Data
public class MenuTreeVO {
  private Integer id; private Integer parentId; private String name; private Integer type;
  private String path; private String component; private String perms; private String icon;
  private Integer sort; private Integer visible; private Integer status;
  private List<MenuTreeVO> children = new ArrayList<>();
}
