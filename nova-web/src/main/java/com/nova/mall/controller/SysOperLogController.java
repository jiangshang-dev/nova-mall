package com.nova.mall.controller;

import com.nova.mall.base.PageResponse;
import com.nova.mall.entity.SysOperLog;
import com.nova.mall.service.SysOperLogService;
import com.nova.mall.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "操作日志")
@RestController
@AllArgsConstructor
@RequestMapping("/api/system/operlog")
public class SysOperLogController {
    private final SysOperLogService sysOperLogService;

    @Operation(summary = "操作日志分页")
    @PreAuthorize("hasAuthority('system:operlog:list')")
    @GetMapping("/page")
    public R<PageResponse<SysOperLog>> page(@RequestParam(defaultValue = "1") long pageNum, @RequestParam(defaultValue = "10") long pageSize,
                                            @RequestParam(required = false) String title, @RequestParam(required = false) String operName) {
        return R.ok(PageResponse.res(sysOperLogService.pageLogs(pageNum, pageSize, title, operName)));
    }
}
