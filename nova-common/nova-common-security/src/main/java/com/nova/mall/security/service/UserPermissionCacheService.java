package com.nova.mall.security.service;

import cn.hutool.json.JSONUtil;
import com.nova.mall.base.LoginUser;
import com.nova.mall.utils.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/** 登录用户权限缓存 */
@Service
@RequiredArgsConstructor
public class UserPermissionCacheService {
    public static final String PERM_CACHE_PREFIX = "nova:user:perms:";
    private final CacheService cacheService;

    public void cache(LoginUser loginUser) {
        if (loginUser == null || loginUser.getId() == null) return;
        cacheService.setEx(PERM_CACHE_PREFIX + loginUser.getId(), JSONUtil.toJsonStr(loginUser), 7, TimeUnit.DAYS);
    }

    public void evict(Integer userId) {
        if (userId != null) cacheService.delete(PERM_CACHE_PREFIX + userId);
    }
}
