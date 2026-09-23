package com.nova.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.mall.dto.AddressSaveDTO;
import com.nova.mall.entity.MallAddress;

import java.util.List;

public interface MallAddressService extends IService<MallAddress> {
    List<MallAddress> listMine();

    MallAddress saveMine(AddressSaveDTO dto);

    void removeMine(Long id);

    void setDefault(Long id);
}
