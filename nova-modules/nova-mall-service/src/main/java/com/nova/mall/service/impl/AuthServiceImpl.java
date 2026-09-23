package com.nova.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nova.mall.base.LoginUser;
import com.nova.mall.dto.LoginDTO;
import com.nova.mall.entity.SysPermission;
import com.nova.mall.entity.SysUser;
import com.nova.mall.entity.SysUserRole;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.mapper.SysPermissionMapper;
import com.nova.mall.mapper.SysUserMapper;
import com.nova.mall.mapper.SysUserRoleMapper;
import com.nova.mall.security.service.TokenBlacklistService;
import com.nova.mall.security.service.UserPermissionCacheService;
import com.nova.mall.service.AuthService;
import com.nova.mall.system.enums.UserFlagEnum;
import com.nova.mall.utils.JwtUtil;
import com.nova.mall.utils.UserUtil;
import com.nova.mall.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final SysUserMapper sysUserMapper;
    private final SysPermissionMapper sysPermissionMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserPermissionCacheService userPermissionCacheService;

    @Override
    public LoginVO login(LoginDTO dto) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, dto.getUsername()).last("LIMIT 1"));
        if (user == null) throw new ServiceException(401, "用户名或密码错误");
        if (user.getStatus() != null && user.getStatus() == 0) throw new ServiceException(403, "账号已禁用");
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) throw new ServiceException(401, "用户名或密码错误");
        String clientType = StrUtil.blankToDefault(dto.getClientType(), "admin");
        if ("admin".equalsIgnoreCase(clientType) && (user.getUserFlag() == null || !UserFlagEnum.ADMIN.getCode().equals(user.getUserFlag()))) {
            throw new ServiceException(403, "非管理员账号，无法登录管理端");
        }
        LoginUser loginUser = buildLoginUser(user);
        String token = JwtUtil.enToken(loginUser);
        loginUser.setToken(token);
        userPermissionCacheService.cache(loginUser);
        LoginVO vo = toLoginVO(loginUser);
        vo.setToken(token);
        vo.setMenus(loadMenus(user.getId(), clientType));
        return vo;
    }

    @Override
    public void logout() {
        LoginUser user = UserUtil.getUser();
        if (user != null && StrUtil.isNotBlank(user.getToken())) {
            tokenBlacklistService.blacklist(user.getToken());
            userPermissionCacheService.evict(user.getId());
        }
    }

    @Override
    public LoginVO info() {
        LoginUser user = UserUtil.getUser();
        if (user == null) throw new ServiceException(401, "未登录");
        LoginUser fresh = buildLoginUser(sysUserMapper.selectById(user.getId()));
        fresh.setToken(user.getToken());
        userPermissionCacheService.cache(fresh);
        LoginVO vo = toLoginVO(fresh);
        vo.setToken(user.getToken());
        boolean admin = user.getUserFlag() == UserFlagEnum.ADMIN;
        vo.setMenus(loadMenus(user.getId(), admin ? "admin" : "app"));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(LoginDTO dto) {
        Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, dto.getUsername()));
        if (count != null && count > 0) throw new ServiceException("用户名已存在");
        SysUser user = new SysUser();
        user.setUserName(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserRealName(dto.getUsername());
        user.setUserFlag(UserFlagEnum.COMMON.getCode());
        user.setStatus(1);
        sysUserMapper.insert(user);
        SysUserRole ur = new SysUserRole();
        ur.setUserId(user.getId());
        ur.setRoleId(2);
        sysUserRoleMapper.insert(ur);
    }

    private LoginUser buildLoginUser(SysUser user) {
        if (user == null) throw new ServiceException(401, "用户不存在");
        LoginUser loginUser = new LoginUser();
        loginUser.setId(user.getId());
        loginUser.setUserName(user.getUserName());
        loginUser.setUserRealName(user.getUserRealName());
        loginUser.setUserFlag(UserFlagEnum.getEnumByCode(user.getUserFlag()));
        List<String> roles = sysPermissionMapper.selectRoleCodesByUserId(user.getId());
        List<String> perms = sysPermissionMapper.selectPermsByUserId(user.getId());
        loginUser.setRoles(roles == null ? Collections.emptyList() : roles);
        loginUser.setPermissions(perms == null ? Collections.emptyList() : perms);
        return loginUser;
    }

    private LoginVO toLoginVO(LoginUser loginUser) {
        LoginVO vo = new LoginVO();
        vo.setUserId(loginUser.getId());
        vo.setUserName(loginUser.getUserName());
        vo.setUserRealName(loginUser.getUserRealName());
        vo.setRoles(loginUser.getRoles());
        vo.setPermissions(loginUser.getPermissions());
        return vo;
    }

    private List<SysPermission> loadMenus(Integer userId, String clientType) {
        if (!"admin".equalsIgnoreCase(clientType)) return Collections.emptyList();
        List<SysPermission> menus = sysPermissionMapper.selectMenusByUserId(userId);
        return menus == null ? Collections.emptyList() : menus;
    }
}
