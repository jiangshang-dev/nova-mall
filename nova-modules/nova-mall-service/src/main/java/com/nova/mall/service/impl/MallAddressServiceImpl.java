package com.nova.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.base.LoginUser;
import com.nova.mall.dto.AddressSaveDTO;
import com.nova.mall.entity.MallAddress;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.mapper.MallAddressMapper;
import com.nova.mall.service.MallAddressService;
import com.nova.mall.utils.UserUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MallAddressServiceImpl extends ServiceImpl<MallAddressMapper, MallAddress> implements MallAddressService {

    private Integer requireUserId() {
        LoginUser user = UserUtil.getUser();
        if (user == null || user.getId() == null) throw new ServiceException(401, "请先登录");
        return user.getId();
    }

    @Override
    public List<MallAddress> listMine() {
        Integer userId = requireUserId();
        return this.list(new LambdaQueryWrapper<MallAddress>()
                .eq(MallAddress::getUserId, userId)
                .orderByDesc(MallAddress::getIsDefault)
                .orderByDesc(MallAddress::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallAddress saveMine(AddressSaveDTO dto) {
        Integer userId = requireUserId();
        MallAddress addr;
        if (dto.getId() != null) {
            addr = this.getById(dto.getId());
            if (addr == null || !userId.equals(addr.getUserId())) throw new ServiceException("地址不存在");
        } else {
            addr = new MallAddress();
            addr.setUserId(userId);
        }
        addr.setReceiverName(dto.getReceiverName().trim());
        addr.setReceiverPhone(dto.getReceiverPhone().trim());
        addr.setProvince(StrUtil.blankToDefault(dto.getProvince(), "").trim());
        addr.setCity(StrUtil.blankToDefault(dto.getCity(), "").trim());
        addr.setDistrict(StrUtil.blankToDefault(dto.getDistrict(), "").trim());
        addr.setDetail(dto.getDetail().trim());
        boolean asDefault = dto.getIsDefault() != null && dto.getIsDefault() == 1;
        long count = this.count(new LambdaQueryWrapper<MallAddress>().eq(MallAddress::getUserId, userId));
        if (count == 0 || asDefault) {
            clearDefault(userId);
            addr.setIsDefault(1);
        } else if (addr.getIsDefault() == null) {
            addr.setIsDefault(0);
        }
        this.saveOrUpdate(addr);
        return addr;
    }

    @Override
    public void removeMine(Long id) {
        Integer userId = requireUserId();
        MallAddress addr = this.getById(id);
        if (addr == null || !userId.equals(addr.getUserId())) throw new ServiceException("地址不存在");
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long id) {
        Integer userId = requireUserId();
        MallAddress addr = this.getById(id);
        if (addr == null || !userId.equals(addr.getUserId())) throw new ServiceException("地址不存在");
        clearDefault(userId);
        addr.setIsDefault(1);
        this.updateById(addr);
    }

    private void clearDefault(Integer userId) {
        this.update(new LambdaUpdateWrapper<MallAddress>()
                .eq(MallAddress::getUserId, userId)
                .set(MallAddress::getIsDefault, 0));
    }
}
