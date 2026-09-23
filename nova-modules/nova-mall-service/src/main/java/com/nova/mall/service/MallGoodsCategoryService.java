package com.nova.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.mall.entity.MallGoodsCategory;

import java.util.List;

public interface MallGoodsCategoryService extends IService<MallGoodsCategory> {
    List<MallGoodsCategory> listAll();
}
