package com.nova.mall.security.service;

import com.nova.mall.utils.CacheService;
import com.nova.mall.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/** Token 黑名单（退出后失效） */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private static final String KEY_PREFIX = "nova:token:blacklist:";
    private final CacheService cacheService;

    public void blacklist(String token) {
        if (token == null || token.isBlank()) return;
        long ttl = resolveTtlMillis(token);
        if (ttl <= 0) ttl = JwtUtil.getExpire() == null ? TimeUnit.DAYS.toMillis(7) : JwtUtil.getExpire();
        cacheService.setEx(KEY_PREFIX + token, "1", ttl, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        if (token == null || token.isBlank()) return false;
        return Boolean.TRUE.equals(cacheService.exists(KEY_PREFIX + token));
    }

    private long resolveTtlMillis(String token) {
        try {
            Date expire = JwtUtil.deToken(token).getExpire();
            if (expire == null) return JwtUtil.getExpire();
            return Math.max(expire.getTime() - System.currentTimeMillis(), 1000L);
        } catch (Exception e) {
            return JwtUtil.getExpire() == null ? TimeUnit.DAYS.toMillis(7) : JwtUtil.getExpire();
        }
    }
}
