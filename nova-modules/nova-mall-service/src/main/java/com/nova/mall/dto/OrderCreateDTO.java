package com.nova.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO {
    @NotNull(message = "请选择收货地址")
    private Long addressId;

    /** alipay / wxpay / cod */
    @NotBlank(message = "请选择支付方式")
    private String payType;

    /** hour / next_day / three_day */
    @NotBlank(message = "请选择配送方式")
    private String deliveryType;

    private String remark;

    /** 指定购物车项；为空则结算全部勾选商品 */
    private List<Long> cartItemIds;

    /** 立即购买：商品ID（与 cartItemIds 二选一） */
    private Integer goodsId;
    private Integer quantity;
}
