package com.nova.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.mall.entity.MallGoods;

public interface MallGoodsService extends IService<MallGoods> {
    Page<MallGoods> pageGoods(long pageNum, long pageSize, String name, Integer status, Integer categoryId);

    Page<MallGoods> pageOnSale(long pageNum, long pageSize, String name, Integer categoryId);
}
