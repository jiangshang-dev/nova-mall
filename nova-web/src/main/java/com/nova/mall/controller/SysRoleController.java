package com.nova.mall.controller;

import com.nova.mall.base.PageResponse;
import com.nova.mall.debounce.annotation.Debounce;
import com.nova.mall.entity.SysRole;
import com.nova.mall.log.annotation.OperLog;
import com.nova.mall.log.enums.BusinessType;
import com.nova.mall.service.SysRoleService;
import com.nova.mall.utils.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@AllArgsConstructor
@RequestMapping("/api/system/role")
public class SysRoleController {
    private final SysRoleService sysRoleService;

    @Operation(summary = "角色分页列表")
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/page")
    public R<PageResponse<SysRole>> page(@RequestParam(defaultValue = "1") long pageNum, @RequestParam(defaultValue = "10") long pageSize,
                                         @RequestParam(required = false) String roleName) {
        return R.ok(PageResponse.res(sysRoleService.pageRoles(pageNum, pageSize, roleName)));
    }

    @Operation(summary = "角色全部列表")
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/list")
    public R<List<SysRole>> list() {
        return R.ok(sysRoleService.list());
    }

    @Operation(summary = "角色已分配权限ID")
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/{id}/permissions")
    public R<List<Integer>> permissions(@PathVariable Integer id) {
        return R.ok(sysRoleService.listPermissionIds(id));
    }

    @Operation(summary = "新增角色")
    @Debounce
    @OperLog(title = "角色管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:role:add')")
    @PostMapping
    public R<Void> add(@RequestBody RoleSaveRequest request) {
        sysRoleService.createRole(request.getRole(), request.getPermissionIds());
        return R.ok();
    }

    @Operation(summary = "修改角色")
    @Debounce
    @OperLog(title = "角色管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping
    public R<Void> edit(@RequestBody RoleSaveRequest request) {
        sysRoleService.updateRole(request.getRole(), request.getPermissionIds());
        return R.ok();
    }

    @Operation(summary = "删除角色")
    @Debounce
    @OperLog(title = "角色管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:role:remove')")
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Integer id) {
        sysRoleService.removeRole(id);
        return R.ok();
    }

    @Data
    public static class RoleSaveRequest {
        private SysRole role;
        private List<Integer> permissionIds;
    }
}
