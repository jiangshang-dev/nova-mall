package com.nova.mall.security.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.nova.mall.base.LoginUser;
import com.nova.mall.security.service.TokenBlacklistService;
import com.nova.mall.utils.CacheService;
import com.nova.mall.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** JWT 认证过滤器 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String PERM_CACHE_PREFIX = "nova:user:perms:";
    private final TokenBlacklistService tokenBlacklistService;
    private final CacheService cacheService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader(JwtUtil.getTokenKey());
        if (StrUtil.isBlank(token)) token = request.getParameter(JwtUtil.getTokenKey());
        if (StrUtil.isNotBlank(token)) {
            try {
                if (tokenBlacklistService.isBlacklisted(token)) {
                    writeUnauthorized(response, "登录已失效，请重新登录");
                    return;
                }
                LoginUser loginUser = JwtUtil.deToken(token);
                fillAuthorities(loginUser);
                List<SimpleGrantedAuthority> authorities = (loginUser.getPermissions() == null
                        ? Collections.<String>emptyList() : loginUser.getPermissions()).stream()
                        .filter(StrUtil::isNotBlank).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(loginUser, null, authorities));
            } catch (Exception e) {
                writeUnauthorized(response, e.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void fillAuthorities(LoginUser loginUser) {
        if (loginUser.getId() == null) return;
        String cache = cacheService.get(PERM_CACHE_PREFIX + loginUser.getId());
        if (StrUtil.isBlank(cache)) return;
        LoginUser cached = JSONUtil.toBean(cache, LoginUser.class);
        if (cached != null) {
            loginUser.setRoles(cached.getRoles());
            loginUser.setPermissions(cached.getPermissions());
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"code\":401,\"msg\":\"" + StrUtil.blankToDefault(msg, "未登录") + "\",\"data\":null}");
    }
}
