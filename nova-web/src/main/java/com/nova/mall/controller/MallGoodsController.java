package com.nova.mall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nova.mall.base.PageResponse;
import com.nova.mall.debounce.annotation.Debounce;
import com.nova.mall.dto.CategorySaveDTO;
import com.nova.mall.entity.MallGoods;
import com.nova.mall.entity.MallGoodsCategory;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.log.annotation.OperLog;
import com.nova.mall.log.enums.BusinessType;
import com.nova.mall.service.MallGoodsCategoryService;
import com.nova.mall.service.MallGoodsService;
import com.nova.mall.utils.R;
import com.nova.mall.vo.CategoryTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品管理")
@RestController
@AllArgsConstructor
@RequestMapping("/api/goods")
public class MallGoodsController {
    private final MallGoodsService mallGoodsService;
    private final MallGoodsCategoryService mallGoodsCategoryService;

    @Operation(summary = "前台商品分页列表")
    @GetMapping("/list")
    public R<PageResponse<MallGoods>> list(@RequestParam(defaultValue = "1") long pageNum, @RequestParam(defaultValue = "10") long pageSize,
                                           @RequestParam(required = false) String name, @RequestParam(required = false) Integer categoryId) {
        return R.ok(PageResponse.res(mallGoodsService.pageOnSale(pageNum, pageSize, name, categoryId)));
    }

    @Operation(summary = "前台商品详情")
    @GetMapping("/{id}")
    public R<MallGoods> detail(@PathVariable Integer id) {
        return R.ok(mallGoodsService.getById(id));
    }

    @Operation(summary = "管理端商品分页")
    @PreAuthorize("hasAuthority('goods:list')")
    @GetMapping("/admin/page")
    public R<PageResponse<MallGoods>> adminPage(@RequestParam(defaultValue = "1") long pageNum, @RequestParam(defaultValue = "10") long pageSize,
                                                @RequestParam(required = false) String name, @RequestParam(required = false) Integer status, @RequestParam(required = false) Integer categoryId) {
        return R.ok(PageResponse.res(mallGoodsService.pageGoods(pageNum, pageSize, name, status, categoryId)));
    }

    @Operation(summary = "新增商品")
    @Debounce
    @OperLog(title = "商品管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('goods:add')")
    @PostMapping("/admin")
    public R<Void> add(@RequestBody MallGoods goods) {
        if (goods.getStatus() == null) goods.setStatus(1);
        mallGoodsService.save(goods);
        return R.ok();
    }

    @Operation(summary = "修改商品")
    @Debounce
    @OperLog(title = "商品管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('goods:edit')")
    @PutMapping("/admin")
    public R<Void> edit(@RequestBody MallGoods goods) {
        mallGoodsService.updateById(goods);
        return R.ok();
    }

    @Operation(summary = "删除商品")
    @Debounce
    @OperLog(title = "商品管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('goods:remove')")
    @DeleteMapping("/admin/{id}")
    public R<Void> remove(@PathVariable Integer id) {
        mallGoodsService.removeById(id);
        return R.ok();
    }

    @Operation(summary = "前台分类列表（扁平，仅启用）")
    @GetMapping("/category/list")
    public R<List<MallGoodsCategory>> categoryList() {
        return R.ok(mallGoodsCategoryService.listAll());
    }

    @Operation(summary = "前台分类树（仅启用）")
    @GetMapping("/category/tree")
    public R<List<CategoryTreeVO>> categoryTree() {
        return R.ok(mallGoodsCategoryService.listTree(true));
    }

    @Operation(summary = "管理端分类列表（扁平）")
    @PreAuthorize("hasAuthority('goods:category:list')")
    @GetMapping("/admin/category/list")
    public R<List<MallGoodsCategory>> adminCategoryList() {
        return R.ok(mallGoodsCategoryService.list(new LambdaQueryWrapper<MallGoodsCategory>()
                .orderByAsc(MallGoodsCategory::getSort)
                .orderByAsc(MallGoodsCategory::getId)));
    }

    @Operation(summary = "管理端分类树")
    @PreAuthorize("hasAuthority('goods:category:list')")
    @GetMapping("/admin/category/tree")
    public R<List<CategoryTreeVO>> adminCategoryTree() {
        return R.ok(mallGoodsCategoryService.listTree(false));
    }

    @Operation(summary = "新增/修改分类")
    @Debounce
    @OperLog(title = "商品分类", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('goods:category:list')")
    @PostMapping("/admin/category")
    public R<MallGoodsCategory> saveCategory(@Valid @RequestBody CategorySaveDTO dto) {
        return R.ok(mallGoodsCategoryService.saveCategory(dto));
    }

    @Operation(summary = "修改分类")
    @Debounce
    @OperLog(title = "商品分类", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('goods:category:list')")
    @PutMapping("/admin/category")
    public R<MallGoodsCategory> updateCategory(@Valid @RequestBody CategorySaveDTO dto) {
        if (dto.getId() == null) throw new ServiceException("分类ID不能为空");
        return R.ok(mallGoodsCategoryService.saveCategory(dto));
    }

    @Operation(summary = "删除分类")
    @Debounce
    @OperLog(title = "商品分类", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('goods:category:list')")
    @DeleteMapping("/admin/category/{id}")
    public R<Void> removeCategory(@PathVariable Integer id) {
        mallGoodsCategoryService.removeCategory(id);
        return R.ok();
    }
}
