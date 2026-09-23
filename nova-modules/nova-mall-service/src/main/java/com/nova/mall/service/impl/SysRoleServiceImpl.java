package com.nova.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.entity.SysRole;
import com.nova.mall.entity.SysRolePermission;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.mapper.SysRoleMapper;
import com.nova.mall.mapper.SysRolePermissionMapper;
import com.nova.mall.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public Page<SysRole> pageRoles(long pageNum, long pageSize, String roleName) {
        return this.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<SysRole>()
                .like(StrUtil.isNotBlank(roleName), SysRole::getRoleName, roleName).orderByAsc(SysRole::getSort));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRole(SysRole role, List<Integer> permissionIds) {
        if (this.count(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, role.getRoleCode())) > 0)
            throw new ServiceException("角色编码已存在");
        if (role.getStatus() == null) role.setStatus(1);
        this.save(role);
        assignPermissions(role.getId(), permissionIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(SysRole role, List<Integer> permissionIds) {
        this.updateById(role);
        if (permissionIds != null) assignPermissions(role.getId(), permissionIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRole(Integer id) {
        if (id != null && id == 1) throw new ServiceException("不允许删除超级管理员角色");
        this.removeById(id);
        sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Integer roleId, List<Integer> permissionIds) {
        sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId));
        if (permissionIds == null) return;
        for (Integer pid : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(pid);
            sysRolePermissionMapper.insert(rp);
        }
    }

    @Override
    public List<Integer> listPermissionIds(Integer roleId) {
        return sysRolePermissionMapper.selectList(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId))
                .stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
    }
}
