package com.nova.mall.controller;

import com.nova.mall.debounce.annotation.Debounce;
import com.nova.mall.dto.CartAddDTO;
import com.nova.mall.log.annotation.OperLog;
import com.nova.mall.log.enums.BusinessType;
import com.nova.mall.service.MallCartService;
import com.nova.mall.utils.R;
import com.nova.mall.vo.CartItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "购物车")
@RestController
@AllArgsConstructor
@RequestMapping("/api/cart")
public class MallCartController {
    private final MallCartService mallCartService;

    @Operation(summary = "我的购物车")
    @GetMapping("/list")
    public R<List<CartItemVO>> list() {
        return R.ok(mallCartService.listMine());
    }

    @Operation(summary = "加入购物车")
    @Debounce
    @OperLog(title = "购物车", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public R<Void> add(@Valid @RequestBody CartAddDTO dto) {
        mallCartService.add(dto);
        return R.ok();
    }

    @Operation(summary = "修改数量")
    @PutMapping("/{id}")
    public R<Void> updateQty(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        mallCartService.updateQuantity(id, body.get("quantity"));
        return R.ok();
    }

    @Operation(summary = "删除购物车项")
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        mallCartService.remove(id);
        return R.ok();
    }
}
