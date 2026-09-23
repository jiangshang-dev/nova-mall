package com.nova.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.entity.MallGoodsCategory;
import com.nova.mall.mapper.MallGoodsCategoryMapper;
import com.nova.mall.service.MallGoodsCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MallGoodsCategoryServiceImpl extends ServiceImpl<MallGoodsCategoryMapper, MallGoodsCategory> implements MallGoodsCategoryService {
    @Override
    public List<MallGoodsCategory> listAll() {
        return this.list(new LambdaQueryWrapper<MallGoodsCategory>().eq(MallGoodsCategory::getStatus, 1).orderByAsc(MallGoodsCategory::getSort));
    }
}
