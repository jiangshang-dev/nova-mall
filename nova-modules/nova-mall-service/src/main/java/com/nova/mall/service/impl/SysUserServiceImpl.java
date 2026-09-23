package com.nova.mall.service.impl;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.entity.SysUser; import com.nova.mall.entity.SysUserRole;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.mapper.SysUserMapper; import com.nova.mall.mapper.SysUserRoleMapper;
import com.nova.mall.security.service.UserPermissionCacheService;
import com.nova.mall.service.SysUserService; import com.nova.mall.system.enums.UserFlagEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
  private final SysUserRoleMapper sysUserRoleMapper;
  private final PasswordEncoder passwordEncoder;
  private final UserPermissionCacheService userPermissionCacheService;
  @Override public Page<SysUser> pageUsers(long pageNum, long pageSize, String userName, Integer status) {
    Page<SysUser> page = this.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<SysUser>()
      .like(StrUtil.isNotBlank(userName), SysUser::getUserName, userName)
      .eq(status != null, SysUser::getStatus, status).orderByDesc(SysUser::getId));
    page.getRecords().forEach(u -> u.setPassword(null)); return page;
  }
  @Override @Transactional(rollbackFor = Exception.class)
  public void createUser(SysUser user, List<Integer> roleIds) {
    if (this.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, user.getUserName())) > 0)
      throw new ServiceException("用户名已存在");
    if (StrUtil.isBlank(user.getPassword())) user.setPassword("admin123");
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    if (user.getUserFlag() == null) user.setUserFlag(UserFlagEnum.ADMIN.getCode());
    if (user.getStatus() == null) user.setStatus(1);
    this.save(user); assignRoles(user.getId(), roleIds);
  }
  @Override @Transactional(rollbackFor = Exception.class)
  public void updateUser(SysUser user, List<Integer> roleIds) {
    user.setPassword(null); this.updateById(user);
    if (roleIds != null) assignRoles(user.getId(), roleIds);
    userPermissionCacheService.evict(user.getId());
  }
  @Override @Transactional(rollbackFor = Exception.class)
  public void removeUser(Integer id) {
    if (id != null && id == 1) throw new ServiceException("不允许删除超级管理员");
    this.removeById(id);
    sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
    userPermissionCacheService.evict(id);
  }
  @Override public void resetPassword(Integer id, String newPassword) {
    SysUser user = new SysUser(); user.setId(id);
    user.setPassword(passwordEncoder.encode(StrUtil.blankToDefault(newPassword, "admin123")));
    this.updateById(user);
  }
  @Override @Transactional(rollbackFor = Exception.class)
  public void assignRoles(Integer userId, List<Integer> roleIds) {
    sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
    if (roleIds == null) return;
    for (Integer roleId : roleIds) {
      SysUserRole ur = new SysUserRole(); ur.setUserId(userId); ur.setRoleId(roleId); sysUserRoleMapper.insert(ur);
    }
    userPermissionCacheService.evict(userId);
  }
}
