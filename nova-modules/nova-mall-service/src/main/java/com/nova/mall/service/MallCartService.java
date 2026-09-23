package com.nova.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.mall.dto.CartAddDTO;
import com.nova.mall.entity.MallCart;
import com.nova.mall.vo.CartItemVO;

import java.util.List;

public interface MallCartService extends IService<MallCart> {
    List<CartItemVO> listMine();

    void add(CartAddDTO dto);

    void updateQuantity(Long id, Integer quantity);

    void remove(Long id);

    void clearChecked();
}
