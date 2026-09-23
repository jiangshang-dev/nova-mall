package com.nova.mall.controller;

import com.nova.mall.base.PageResponse;
import com.nova.mall.debounce.annotation.Debounce;
import com.nova.mall.dto.GoodsCommentDTO;
import com.nova.mall.entity.MallGoodsComment;
import com.nova.mall.log.annotation.OperLog;
import com.nova.mall.log.enums.BusinessType;
import com.nova.mall.service.MallGoodsCommentService;
import com.nova.mall.utils.R;
import com.nova.mall.vo.GoodsCommentSummaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品评价")
@RestController
@AllArgsConstructor
@RequestMapping("/api/goods")
public class MallGoodsCommentController {
    private final MallGoodsCommentService mallGoodsCommentService;

    @Operation(summary = "商品评价列表与均分")
    @GetMapping("/{id}/comments")
    public R<GoodsCommentSummaryVO> comments(@PathVariable("id") Integer id) {
        return R.ok(mallGoodsCommentService.summary(id));
    }

    @Operation(summary = "发表评价")
    @Debounce
    @OperLog(title = "商品评价", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/comments")
    public R<Void> add(@PathVariable("id") Integer id, @Valid @RequestBody GoodsCommentDTO dto) {
        mallGoodsCommentService.addComment(id, dto);
        return R.ok();
    }

    @Operation(summary = "后台评价分页")
    @PreAuthorize("hasAuthority('goods:comment:list')")
    @GetMapping("/admin/comments/page")
    public R<PageResponse<MallGoodsComment>> adminPage(@RequestParam(defaultValue = "1") long pageNum,
                                                       @RequestParam(defaultValue = "10") long pageSize,
                                                       @RequestParam(required = false) Integer goodsId,
                                                       @RequestParam(required = false) Integer status) {
        return R.ok(PageResponse.res(mallGoodsCommentService.pageComments(pageNum, pageSize, goodsId, status)));
    }

    @Operation(summary = "显示/隐藏评价")
    @PreAuthorize("hasAuthority('goods:comment:list')")
    @OperLog(title = "商品评价", businessType = BusinessType.UPDATE)
    @PutMapping("/admin/comments/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        mallGoodsCommentService.updateStatus(id, status);
        return R.ok();
    }
}
