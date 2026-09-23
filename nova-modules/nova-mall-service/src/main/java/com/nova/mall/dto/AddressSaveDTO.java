package com.nova.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressSaveDTO {
    private Long id;

    @NotBlank(message = "请填写收货人姓名")
    @Size(max = 32)
    private String receiverName;

    @NotBlank(message = "请填写手机号")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String receiverPhone;

    private String province;
    private String city;
    private String district;

    @NotBlank(message = "请填写详细地址")
    @Size(max = 200)
    private String detail;

    private Integer isDefault;
}
