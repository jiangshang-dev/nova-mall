package com.nova.mall.controller;

import com.nova.mall.debounce.annotation.Debounce;
import com.nova.mall.entity.MallFreightSetting;
import com.nova.mall.log.annotation.OperLog;
import com.nova.mall.log.enums.BusinessType;
import com.nova.mall.service.MallFreightService;
import com.nova.mall.utils.R;
import com.nova.mall.vo.ShippingOptionsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "运费配送")
@RestController
@AllArgsConstructor
@RequestMapping("/api/freight")
public class MallFreightController {
    private final MallFreightService mallFreightService;

    @Operation(summary = "前台配送运费选项")
    @GetMapping("/options")
    public R<ShippingOptionsVO> options(@RequestParam(required = false) BigDecimal goodsAmount) {
        return R.ok(mallFreightService.optionsForAmount(goodsAmount));
    }

    @Operation(summary = "后台运费列表")
    @PreAuthorize("hasAuthority('shop:setting')")
    @GetMapping("/admin/list")
    public R<List<MallFreightSetting>> adminList() {
        return R.ok(mallFreightService.listAdmin());
    }

    @Operation(summary = "保存运费配置")
    @Debounce
    @OperLog(title = "运费设置", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('shop:setting')")
    @PostMapping("/admin/save")
    public R<Void> save(@RequestBody MallFreightSetting setting) {
        mallFreightService.saveSetting(setting);
        return R.ok();
    }

    @Operation(summary = "获取/设置配送城市")
    @PreAuthorize("hasAuthority('shop:setting')")
    @GetMapping("/admin/service-city")
    public R<String> getCity() {
        return R.ok(mallFreightService.getServiceCity());
    }

    @PreAuthorize("hasAuthority('shop:setting')")
    @PutMapping("/admin/service-city")
    public R<Void> setCity(@RequestBody Map<String, String> body) {
        mallFreightService.setServiceCity(body.get("city"));
        return R.ok();
    }
}
