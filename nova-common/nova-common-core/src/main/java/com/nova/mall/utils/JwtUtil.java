package com.nova.mall.utils;

import cn.hutool.core.util.StrUtil;
import com.nova.mall.base.LoginUser;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.system.enums.UserFlagEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

/**
 * jwt生成工具（对齐 material-app：userFlag 存枚举名，解析用 valueOf）
 */
@Slf4j
@Component
public class JwtUtil {

    private static Long expire;
    private static SecretKey secretKey;
    private static String tokenKey;

    public JwtUtil(@Value("${nova-mall.security.jwt-secret:nova-mall-jwt-secret-key-32bytes!!}") String secret,
                   @Value("${nova-mall.security.token-name:Access-Token}") String tokenName,
                   @Value("${nova-mall.security.expire:604800000}") Long expireMillis) {
        JwtUtil.expire = expireMillis;
        JwtUtil.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        JwtUtil.tokenKey = tokenName;
    }

    public static String getTokenKey() { return tokenKey; }
    public static Long getExpire() { return expire; }

    /**
     * 根据登录用户生成token
     */
    public static String enToken(LoginUser user) {
        HashMap<String, Object> claims = new HashMap<>(4);
        claims.put("id", user.getId());
        claims.put("userName", user.getUserName());
        claims.put("userRealName", user.getUserRealName());
        // 与 material-app 一致：写入枚举名，例如 ADMIN
        claims.put("userFlag", user.getUserFlag() == null ? null : user.getUserFlag().name());
        long expMillis = System.currentTimeMillis() + JwtUtil.expire;
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .signWith(JwtUtil.secretKey)
                .expiration(new Date(expMillis))
                .compact();
    }

    public static LoginUser deToken(HttpServletRequest request) {
        String token = request.getHeader(JwtUtil.tokenKey);
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(JwtUtil.tokenKey);
        }
        return JwtUtil.deToken(token);
    }

    public static LoginUser deToken(String token) {
        if (StrUtil.isBlank(token)) {
            throw new ServiceException("未登录");
        }
        try {
            Claims claims = Jwts.parser().verifyWith(JwtUtil.secretKey).build().parseSignedClaims(token).getPayload();
            LoginUser user = new LoginUser();
            user.setId(claims.get("id", Integer.class));
            user.setUserName(claims.get("userName", String.class));
            user.setUserRealName(claims.get("userRealName", String.class));
            Object userFlag = claims.get("userFlag");
            if (userFlag instanceof String s && StrUtil.isNotBlank(s)) {
                user.setUserFlag(UserFlagEnum.valueOf(s));
            } else if (userFlag instanceof Number n) {
                // 兼容曾写入数字 code 的 token
                user.setUserFlag(UserFlagEnum.getEnumByCode(n.intValue()));
            }
            user.setExpire(claims.getExpiration());
            user.setToken(token);
            return user;
        } catch (MalformedJwtException e) {
            throw new ServiceException("令牌不合法");
        } catch (ExpiredJwtException e) {
            throw new ServiceException("登录已过期");
        } catch (IllegalArgumentException e) {
            throw new ServiceException("令牌不合法");
        }
    }
}
