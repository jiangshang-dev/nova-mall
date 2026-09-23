package com.nova.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.mall.entity.SysPermission;

import java.util.List;

public interface SysPermissionService extends IService<SysPermission> {
    List<SysPermission> listTree();

    void createPermission(SysPermission permission);

    void updatePermission(SysPermission permission);

    void removePermission(Integer id);
}
