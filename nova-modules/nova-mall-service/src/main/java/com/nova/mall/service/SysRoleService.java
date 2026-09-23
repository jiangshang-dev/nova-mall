package com.nova.mall.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.mall.entity.SysRole;
import java.util.List;
public interface SysRoleService extends IService<SysRole> {
  Page<SysRole> pageRoles(long pageNum, long pageSize, String roleName);
  void createRole(SysRole role, List<Integer> permissionIds);
  void updateRole(SysRole role, List<Integer> permissionIds);
  void removeRole(Integer id);
  void assignPermissions(Integer roleId, List<Integer> permissionIds);
  List<Integer> listPermissionIds(Integer roleId);
}
