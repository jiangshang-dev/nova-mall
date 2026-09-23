package com.nova.mall.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GoodsCommentDTO {
    @NotNull
    @Min(1)
    @Max(5)
    private Integer star;
    @NotBlank
    private String content;
}
