package com.nova.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategorySaveDTO {
    private Integer id;

    /** 父分类ID，0 表示顶级 */
    @NotNull(message = "请选择上级分类")
    private Integer parentId = 0;

    @NotBlank(message = "请填写分类名称")
    @Size(max = 64)
    private String name;

    private Integer sort = 0;

    /** 1启用 0停用 */
    private Integer status = 1;
}
