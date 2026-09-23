package com.nova.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.mall.entity.MallFreightSetting;
import com.nova.mall.vo.FreightOptionVO;
import com.nova.mall.vo.ShippingOptionsVO;

import java.math.BigDecimal;
import java.util.List;

public interface MallFreightService extends IService<MallFreightSetting> {
    ShippingOptionsVO optionsForAmount(BigDecimal goodsAmount);

    FreightOptionVO calc(String deliveryType, BigDecimal goodsAmount);

    List<MallFreightSetting> listAdmin();

    void saveSetting(MallFreightSetting setting);

    String getServiceCity();

    void setServiceCity(String city);
}
