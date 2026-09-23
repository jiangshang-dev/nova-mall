package com.nova.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.entity.MallGoods;
import com.nova.mall.mapper.MallGoodsMapper;
import com.nova.mall.mq.producer.MessageProducer;
import com.nova.mall.service.MallGoodsCategoryService;
import com.nova.mall.service.MallGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MallGoodsServiceImpl extends ServiceImpl<MallGoodsMapper, MallGoods> implements MallGoodsService {
    private MessageProducer messageProducer;
    private MallGoodsCategoryService mallGoodsCategoryService;

    @Autowired(required = false)
    public void setMessageProducer(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @Lazy
    @Autowired
    public void setMallGoodsCategoryService(MallGoodsCategoryService mallGoodsCategoryService) {
        this.mallGoodsCategoryService = mallGoodsCategoryService;
    }

    @Override
    public Page<MallGoods> pageGoods(long pageNum, long pageSize, String name, Integer status, Integer categoryId) {
        LambdaQueryWrapper<MallGoods> qw = new LambdaQueryWrapper<MallGoods>()
                .like(StrUtil.isNotBlank(name), MallGoods::getName, name)
                .eq(status != null, MallGoods::getStatus, status)
                .orderByDesc(MallGoods::getId);
        applyCategoryFilter(qw, categoryId);
        return this.page(new Page<>(pageNum, pageSize), qw);
    }

    @Override
    public Page<MallGoods> pageOnSale(long pageNum, long pageSize, String name, Integer categoryId) {
        LambdaQueryWrapper<MallGoods> qw = new LambdaQueryWrapper<MallGoods>()
                .eq(MallGoods::getStatus, 1)
                .like(StrUtil.isNotBlank(name), MallGoods::getName, name)
                .orderByDesc(MallGoods::getId);
        applyCategoryFilter(qw, categoryId);
        return this.page(new Page<>(pageNum, pageSize), qw);
    }

    private void applyCategoryFilter(LambdaQueryWrapper<MallGoods> qw, Integer categoryId) {
        if (categoryId == null || mallGoodsCategoryService == null) return;
        Set<Integer> ids = mallGoodsCategoryService.collectSelfAndDescendantIds(categoryId);
        if (ids.isEmpty()) {
            qw.eq(MallGoods::getCategoryId, categoryId);
        } else {
            qw.in(MallGoods::getCategoryId, ids);
        }
    }

    @Override
    public boolean save(MallGoods entity) {
        boolean ok = super.save(entity);
        publishSync(entity);
        return ok;
    }

    @Override
    public boolean updateById(MallGoods entity) {
        boolean ok = super.updateById(entity);
        publishSync(entity);
        return ok;
    }

    private void publishSync(MallGoods entity) {
        if (messageProducer != null && entity != null) messageProducer.sendGoodsSync(JSONUtil.toJsonStr(entity));
    }
}
