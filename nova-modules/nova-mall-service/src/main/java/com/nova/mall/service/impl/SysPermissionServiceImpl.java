package com.nova.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.entity.SysPermission;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.mapper.SysPermissionMapper;
import com.nova.mall.service.SysPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {
    @Override
    public List<SysPermission> listTree() {
        return this.list(new LambdaQueryWrapper<SysPermission>().orderByAsc(SysPermission::getSort).orderByAsc(SysPermission::getId));
    }

    @Override
    public void createPermission(SysPermission permission) {
        if (permission.getParentId() == null) permission.setParentId(0);
        if (permission.getStatus() == null) permission.setStatus(1);
        if (permission.getVisible() == null) permission.setVisible(1);
        this.save(permission);
    }

    @Override
    public void updatePermission(SysPermission permission) {
        this.updateById(permission);
    }

    @Override
    public void removePermission(Integer id) {
        if (this.count(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getParentId, id)) > 0)
            throw new ServiceException("请先删除子菜单");
        this.removeById(id);
    }
}
