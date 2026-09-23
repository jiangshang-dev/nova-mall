package com.nova.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.base.LoginUser;
import com.nova.mall.dto.GoodsCommentDTO;
import com.nova.mall.entity.MallGoods;
import com.nova.mall.entity.MallGoodsComment;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.mapper.MallGoodsCommentMapper;
import com.nova.mall.mapper.MallGoodsMapper;
import com.nova.mall.service.MallGoodsCommentService;
import com.nova.mall.utils.UserUtil;
import com.nova.mall.vo.GoodsCommentSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MallGoodsCommentServiceImpl extends ServiceImpl<MallGoodsCommentMapper, MallGoodsComment>
        implements MallGoodsCommentService {
    private final MallGoodsMapper mallGoodsMapper;

    @Override
    public GoodsCommentSummaryVO summary(Integer goodsId) {
        List<MallGoodsComment> list = this.list(new LambdaQueryWrapper<MallGoodsComment>()
                .eq(MallGoodsComment::getGoodsId, goodsId)
                .eq(MallGoodsComment::getStatus, 1)
                .orderByDesc(MallGoodsComment::getId)
                .last("LIMIT 50"));
        GoodsCommentSummaryVO vo = new GoodsCommentSummaryVO();
        vo.setList(list);
        vo.setTotal((long) list.size());
        if (list.isEmpty()) {
            vo.setAvgStar(5.0);
        } else {
            double avg = list.stream().mapToInt(c -> c.getStar() == null ? 5 : c.getStar()).average().orElse(5);
            vo.setAvgStar(Math.round(avg * 10) / 10.0);
        }
        return vo;
    }

    @Override
    public void addComment(Integer goodsId, GoodsCommentDTO dto) {
        LoginUser user = UserUtil.getUser();
        if (user == null) throw new ServiceException(401, "请先登录");
        MallGoods goods = mallGoodsMapper.selectById(goodsId);
        if (goods == null || (goods.getStatus() != null && goods.getStatus() == 0)) {
            throw new ServiceException("商品不存在或已下架");
        }
        if (StrUtil.isBlank(dto.getContent())) throw new ServiceException("请填写评价内容");
        MallGoodsComment comment = new MallGoodsComment();
        comment.setGoodsId(goodsId);
        comment.setUserId(user.getId());
        comment.setUserName(StrUtil.blankToDefault(user.getUserRealName(), user.getUserName()));
        comment.setStar(dto.getStar());
        comment.setContent(dto.getContent().trim());
        comment.setStatus(1);
        this.save(comment);
    }

    @Override
    public Page<MallGoodsComment> pageComments(long pageNum, long pageSize, Integer goodsId, Integer status) {
        return this.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<MallGoodsComment>()
                .eq(goodsId != null, MallGoodsComment::getGoodsId, goodsId)
                .eq(status != null, MallGoodsComment::getStatus, status)
                .orderByDesc(MallGoodsComment::getId));
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        MallGoodsComment comment = this.getById(id);
        if (comment == null) throw new ServiceException("评价不存在");
        comment.setStatus(status);
        this.updateById(comment);
    }
}
