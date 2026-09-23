package com.nova.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.mall.dto.GoodsCommentDTO;
import com.nova.mall.entity.MallGoodsComment;
import com.nova.mall.vo.GoodsCommentSummaryVO;

public interface MallGoodsCommentService extends IService<MallGoodsComment> {
    GoodsCommentSummaryVO summary(Integer goodsId);

    void addComment(Integer goodsId, GoodsCommentDTO dto);

    Page<MallGoodsComment> pageComments(long pageNum, long pageSize, Integer goodsId, Integer status);

    void updateStatus(Long id, Integer status);
}
