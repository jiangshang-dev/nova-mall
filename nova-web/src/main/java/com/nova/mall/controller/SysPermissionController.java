package com.nova.mall.controller;

import cn.hutool.core.bean.BeanUtil;
import com.nova.mall.debounce.annotation.Debounce;
import com.nova.mall.entity.SysPermission;
import com.nova.mall.log.annotation.OperLog;
import com.nova.mall.log.enums.BusinessType;
import com.nova.mall.service.SysPermissionService;
import com.nova.mall.utils.R;
import com.nova.mall.vo.MenuTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "菜单管理")
@RestController
@AllArgsConstructor
@RequestMapping("/api/system/menu")
public class SysPermissionController {
    private final SysPermissionService sysPermissionService;

    @Operation(summary = "菜单树")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/tree")
    public R<List<MenuTreeVO>> tree() {
        return R.ok(buildTree(sysPermissionService.listTree()));
    }

    @Operation(summary = "菜单扁平列表")
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/list")
    public R<List<SysPermission>> list() {
        return R.ok(sysPermissionService.listTree());
    }

    @Operation(summary = "新增菜单")
    @Debounce
    @OperLog(title = "菜单管理", businessType = BusinessType.INSERT)
    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping
    public R<Void> add(@RequestBody SysPermission permission) {
        sysPermissionService.createPermission(permission);
        return R.ok();
    }

    @Operation(summary = "修改菜单")
    @Debounce
    @OperLog(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping
    public R<Void> edit(@RequestBody SysPermission permission) {
        sysPermissionService.updatePermission(permission);
        return R.ok();
    }

    @Operation(summary = "删除菜单")
    @Debounce
    @OperLog(title = "菜单管理", businessType = BusinessType.DELETE)
    @PreAuthorize("hasAuthority('system:menu:remove')")
    @DeleteMapping("/{id}")
    public R<Void> remove(@PathVariable Integer id) {
        sysPermissionService.removePermission(id);
        return R.ok();
    }

    private List<MenuTreeVO> buildTree(List<SysPermission> list) {
        List<MenuTreeVO> nodes = list.stream().map(p -> {
            MenuTreeVO vo = BeanUtil.copyProperties(p, MenuTreeVO.class);
            vo.setChildren(new ArrayList<>());
            return vo;
        }).collect(Collectors.toList());
        List<MenuTreeVO> roots = new ArrayList<>();
        for (MenuTreeVO node : nodes) {
            if (node.getParentId() == null || node.getParentId() == 0) {
                roots.add(node);
                continue;
            }
            for (MenuTreeVO parent : nodes) {
                if (Objects.equals(parent.getId(), node.getParentId())) {
                    parent.getChildren().add(node);
                    break;
                }
            }
        }
        return roots;
    }
}
