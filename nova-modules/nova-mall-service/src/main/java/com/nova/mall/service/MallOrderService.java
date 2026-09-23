package com.nova.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.mall.dto.OrderCreateDTO;
import com.nova.mall.entity.MallOrder;
import com.nova.mall.vo.OrderVO;
import com.nova.mall.vo.PayInfoVO;

public interface MallOrderService extends IService<MallOrder> {
    OrderVO create(OrderCreateDTO dto);

    OrderVO detailMine(String orderNo);

    Page<OrderVO> pageMine(long pageNum, long pageSize);

    Page<OrderVO> pageAdmin(long pageNum, long pageSize, String orderNo, Integer status);

    PayInfoVO preparePay(String orderNo, String payType);

    OrderVO mockPaySuccess(String orderNo);

    void cancelMine(String orderNo);

    /** 后台核销货到付款订单（完成） */
    OrderVO verifyCod(String orderNo);

    /** 后台发货 */
    OrderVO ship(String orderNo);

    boolean hasPurchased(Integer userId, Integer goodsId);
}
