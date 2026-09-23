package com.nova.mall.controller;

import com.nova.mall.debounce.annotation.Debounce;
import com.nova.mall.dto.AddressSaveDTO;
import com.nova.mall.entity.MallAddress;
import com.nova.mall.log.annotation.OperLog;
import com.nova.mall.log.enums.BusinessType;
import com.nova.mall.service.MallAddressService;
import com.nova.mall.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "收货地址")
@RestController
@AllArgsConstructor
@RequestMapping("/api/address")
public class MallAddressController {
    private final MallAddressService mallAddressService;

    @Operation(summary = "我的地址列表")
    @GetMapping("/list")
    public R<List<MallAddress>> list() {
        return R.ok(mallAddressService.listMine());
    }

    @Operation(summary = "新增/修改地址")
    @Debounce
    @OperLog(title = "收货地址", businessType = BusinessType.UPDATE)
    @PostMapping("/save")
    public R<MallAddress> save(@Valid @RequestBody AddressSaveDTO dto) {
        return R.ok(mallAddressService.saveMine(dto));
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Long id) {
        mallAddressService.removeMine(id);
        return R.ok();
    }

    @Operation(summary = "设为默认")
    @PutMapping("/{id}/default")
    public R<Void> setDefault(@PathVariable Long id) {
        mallAddressService.setDefault(id);
        return R.ok();
    }
}
