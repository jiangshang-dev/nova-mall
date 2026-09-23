package com.nova.mall.security.service;

import cn.hutool.json.JSONObject;
import com.nova.mall.base.LoginUser;
import com.nova.mall.utils.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 登录用户权限缓存
 * 参考 material-app：不把整份 LoginUser（含枚举）用 Hutool toBean 反序列化，
 * 只缓存 roles / permissions，枚举仍从 JWT 解析。
 */
@Service
@RequiredArgsConstructor
public class UserPermissionCacheService {
    public static final String PERM_CACHE_PREFIX = "nova:user:perms:";
    private final CacheService cacheService;

    public void cache(LoginUser loginUser) {
        if (loginUser == null || loginUser.getId() == null) return;
        JSONObject payload = new JSONObject();
        payload.set("roles", loginUser.getRoles());
        payload.set("permissions", loginUser.getPermissions());
        cacheService.setEx(PERM_CACHE_PREFIX + loginUser.getId(), payload.toString(), 7, TimeUnit.DAYS);
    }

    public void evict(Integer userId) {
        if (userId != null) cacheService.delete(PERM_CACHE_PREFIX + userId);
    }
}
