package com.nova.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.entity.MallFreightSetting;
import com.nova.mall.entity.MallShopConfig;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.mapper.MallFreightSettingMapper;
import com.nova.mall.mapper.MallShopConfigMapper;
import com.nova.mall.service.MallFreightService;
import com.nova.mall.vo.FreightOptionVO;
import com.nova.mall.vo.ShippingOptionsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MallFreightServiceImpl extends ServiceImpl<MallFreightSettingMapper, MallFreightSetting>
        implements MallFreightService {
    private final MallShopConfigMapper shopConfigMapper;

    @Override
    public String getServiceCity() {
        MallShopConfig cfg = shopConfigMapper.selectById("service_city");
        return cfg == null || StrUtil.isBlank(cfg.getConfigValue()) ? "北京市" : cfg.getConfigValue().trim();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setServiceCity(String city) {
        if (StrUtil.isBlank(city)) throw new ServiceException("请填写配送城市");
        MallShopConfig cfg = shopConfigMapper.selectById("service_city");
        if (cfg == null) {
            cfg = new MallShopConfig();
            cfg.setConfigKey("service_city");
            cfg.setRemark("仅配送该城市（不送外地）");
        }
        cfg.setConfigValue(city.trim());
        cfg.setUpdateTime(System.currentTimeMillis());
        if (shopConfigMapper.selectById("service_city") == null) {
            shopConfigMapper.insert(cfg);
        } else {
            shopConfigMapper.updateById(cfg);
        }
    }

    @Override
    public ShippingOptionsVO optionsForAmount(BigDecimal goodsAmount) {
        BigDecimal amount = goodsAmount == null ? BigDecimal.ZERO : goodsAmount;
        List<MallFreightSetting> list = this.list(new LambdaQueryWrapper<MallFreightSetting>()
                .eq(MallFreightSetting::getEnabled, 1)
                .orderByAsc(MallFreightSetting::getSort)
                .orderByAsc(MallFreightSetting::getId));
        List<FreightOptionVO> options = new ArrayList<>();
        for (MallFreightSetting s : list) {
            options.add(toOption(s, amount));
        }
        ShippingOptionsVO vo = new ShippingOptionsVO();
        vo.setServiceCity(getServiceCity());
        vo.setServiceTip("仅支持「" + getServiceCity() + "」同城配送，暂不支持外地");
        vo.setOptions(options);
        vo.setGoodsAmount(amount);
        return vo;
    }

    @Override
    public FreightOptionVO calc(String deliveryType, BigDecimal goodsAmount) {
        MallFreightSetting s = this.getOne(new LambdaQueryWrapper<MallFreightSetting>()
                .eq(MallFreightSetting::getDeliveryType, deliveryType)
                .eq(MallFreightSetting::getEnabled, 1)
                .last("LIMIT 1"));
        if (s == null) throw new ServiceException("配送方式不可用");
        return toOption(s, goodsAmount == null ? BigDecimal.ZERO : goodsAmount);
    }

    @Override
    public List<MallFreightSetting> listAdmin() {
        return this.list(new LambdaQueryWrapper<MallFreightSetting>()
                .orderByAsc(MallFreightSetting::getSort)
                .orderByAsc(MallFreightSetting::getId));
    }

    @Override
    public void saveSetting(MallFreightSetting setting) {
        if (setting == null || StrUtil.isBlank(setting.getDeliveryType())) {
            throw new ServiceException("配送方式不能为空");
        }
        if (setting.getFreight() == null) setting.setFreight(BigDecimal.ZERO);
        if (setting.getEnabled() == null) setting.setEnabled(1);
        if (setting.getSort() == null) setting.setSort(0);
        MallFreightSetting exist = this.getOne(new LambdaQueryWrapper<MallFreightSetting>()
                .eq(MallFreightSetting::getDeliveryType, setting.getDeliveryType())
                .last("LIMIT 1"));
        if (exist != null) {
            setting.setId(exist.getId());
            this.updateById(setting);
        } else {
            this.save(setting);
        }
    }

    private FreightOptionVO toOption(MallFreightSetting s, BigDecimal goodsAmount) {
        FreightOptionVO vo = new FreightOptionVO();
        vo.setDeliveryType(s.getDeliveryType());
        vo.setDeliveryName(s.getDeliveryName());
        vo.setFreight(nz(s.getFreight()));
        vo.setFreeThreshold(s.getFreeThreshold());
        boolean free = s.getFreeThreshold() != null
                && goodsAmount.compareTo(s.getFreeThreshold()) >= 0;
        // 运费本身为 0 也算免运费展示
        if (nz(s.getFreight()).compareTo(BigDecimal.ZERO) == 0) free = true;
        vo.setFreeShipping(free);
        vo.setPayableFreight(free ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : nz(s.getFreight()));
        if (free && s.getFreeThreshold() != null) {
            vo.setTip("满" + s.getFreeThreshold().stripTrailingZeros().toPlainString() + "元免运费");
        } else if (nz(s.getFreight()).compareTo(BigDecimal.ZERO) == 0) {
            vo.setTip("免运费");
        } else {
            vo.setTip("运费 ¥" + nz(s.getFreight()).toPlainString());
        }
        return vo;
    }

    private static BigDecimal nz(BigDecimal v) {
        return (v == null ? BigDecimal.ZERO : v).setScale(2, RoundingMode.HALF_UP);
    }
}
