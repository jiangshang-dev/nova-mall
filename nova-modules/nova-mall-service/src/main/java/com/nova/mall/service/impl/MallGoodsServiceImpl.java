package com.nova.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.entity.MallGoods;
import com.nova.mall.mapper.MallGoodsMapper;
import com.nova.mall.mq.producer.MessageProducer;
import com.nova.mall.service.MallGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MallGoodsServiceImpl extends ServiceImpl<MallGoodsMapper, MallGoods> implements MallGoodsService {
    private MessageProducer messageProducer;

    @Autowired(required = false)
    public void setMessageProducer(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @Override
    public Page<MallGoods> pageGoods(long pageNum, long pageSize, String name, Integer status, Integer categoryId) {
        return this.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<MallGoods>()
                .like(StrUtil.isNotBlank(name), MallGoods::getName, name)
                .eq(status != null, MallGoods::getStatus, status)
                .eq(categoryId != null, MallGoods::getCategoryId, categoryId).orderByDesc(MallGoods::getId));
    }

    @Override
    public Page<MallGoods> pageOnSale(long pageNum, long pageSize, String name, Integer categoryId) {
        return this.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<MallGoods>()
                .eq(MallGoods::getStatus, 1).like(StrUtil.isNotBlank(name), MallGoods::getName, name)
                .eq(categoryId != null, MallGoods::getCategoryId, categoryId).orderByDesc(MallGoods::getId));
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
