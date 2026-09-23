package com.nova.mall.vo;

import lombok.Data;
import java.util.List;

@Data
public class GoodsCommentSummaryVO {
    private Double avgStar;
    private Long total;
    private List<com.nova.mall.entity.MallGoodsComment> list;
}
