package com.nova.mall.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.mall.entity.SysUser;
import java.util.List;
public interface SysUserService extends IService<SysUser> {
  Page<SysUser> pageUsers(long pageNum, long pageSize, String userName, Integer status);
  void createUser(SysUser user, List<Integer> roleIds);
  void updateUser(SysUser user, List<Integer> roleIds);
  void removeUser(Integer id);
  void resetPassword(Integer id, String newPassword);
  void assignRoles(Integer userId, List<Integer> roleIds);
}
