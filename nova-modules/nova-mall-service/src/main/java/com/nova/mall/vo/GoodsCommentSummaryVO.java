package com.nova.mall.vo;

import lombok.Data;
import java.util.List;

@Data
public class GoodsCommentSummaryVO {
    private Double avgStar;
    private Long total;
    /** 当前登录用户是否可评价（已购买且已支付/完成） */
    private Boolean canComment;
    private String commentTip;
    private List<com.nova.mall.entity.MallGoodsComment> list;
}
