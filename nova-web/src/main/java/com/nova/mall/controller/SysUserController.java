package com.nova.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nova.mall.base.PageResponse;
import com.nova.mall.debounce.annotation.Debounce;
import com.nova.mall.entity.SysUser;
import com.nova.mall.log.annotation.OperLog;
import com.nova.mall.log.enums.BusinessType;
import com.nova.mall.service.SysUserService;
import com.nova.mall.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户管理")
@RestController
@AllArgsConstructor
@RequestMapping("/api/system/user")
public class SysUserController {
    private final SysUserService sysUserService;

    @Operation(summary = "用户分页列表")
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/page")
    public R<PageResponse<SysUser>> page(@RequestParam(defaultValue = "1") long pageNum, @RequestParam(defaultValue = "10") long pageSize,
                                         @RequestParam(required = false) String userName, @RequestParam(required = false) Integer status) {
        return R.ok(PageResponse.res(sysUserService.pageUsers(pageNum, pageSize, userName, status)));
    }

    @Operation(summary = "新增用户")
    @Debounce
    @OperLog(title = "用户管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:user:add')")
    @PostMapping
    public R<Void> add(@RequestBody UserSaveRequest request) {
        sysUserService.createUser(request.getUser(), request.getRoleIds());
        return R.ok();
    }

    @Operation(summary = "修改用户")
    @Debounce
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping
    public R<Void> edit(@RequestBody UserSaveRequest request) {
        sysUserService.updateUser(request.getUser(), request.getRoleIds());
        return R.ok();
    }

    @Operation(summary = "删除用户")
    @Debounce
    @OperLog(title = "用户管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:user:remove')")
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Integer id) {
        sysUserService.removeUser(id);
        return R.ok();
    }

    @Operation(summary = "重置密码")
    @Debounce
    @OperLog(title = "用户管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/{id}/reset-password")
    public R<Void> resetPassword(@PathVariable Integer id, @RequestParam(required = false) String password) {
        sysUserService.resetPassword(id, password);
        return R.ok();
    }

    @Data
    public static class UserSaveRequest {
        private SysUser user;
        private List<Integer> roleIds;
    }
}
