package com.nova.mall.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryTreeVO {
    private Integer id;
    private Integer parentId;
    private String name;
    private Integer sort;
    private Integer status;
    private List<CategoryTreeVO> children = new ArrayList<>();
}
