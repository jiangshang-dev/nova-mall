package com.nova.mall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartAddDTO {
    @NotNull
    private Integer goodsId;
    @Min(1)
    private Integer quantity = 1;
}
