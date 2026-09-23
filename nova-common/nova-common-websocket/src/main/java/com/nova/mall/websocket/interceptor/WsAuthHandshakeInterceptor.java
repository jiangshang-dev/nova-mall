package com.nova.mall.websocket.interceptor;

import cn.hutool.core.util.StrUtil;
import com.nova.mall.base.LoginUser;
import com.nova.mall.util.CommonUtil;
import com.nova.mall.utils.JwtUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * WebSocket 握手鉴权：URL ?Access-Token=xxx&role=member|agent
 */
@Component
public class WsAuthHandshakeInterceptor implements HandshakeInterceptor {
    public static final String ATTR_USER = "loginUser";
    public static final String ATTR_ROLE = "csRole";
    public static final String ATTR_CLIENT_IP = "clientIp";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }
        var http = servletRequest.getServletRequest();
        String token = http.getParameter(JwtUtil.getTokenKey());
        if (StrUtil.isBlank(token)) {
            token = http.getHeader(JwtUtil.getTokenKey());
        }
        if (StrUtil.isBlank(token)) {
            return false;
        }
        try {
            LoginUser user = JwtUtil.deToken(token);
            if (user == null || user.getId() == null) {
                return false;
            }
            attributes.put(ATTR_USER, user);
            String role = StrUtil.blankToDefault(http.getParameter("role"), "member");
            attributes.put(ATTR_ROLE, "agent".equalsIgnoreCase(role) ? "agent" : "member");
            attributes.put(ATTR_CLIENT_IP, resolveClientIp(request, http));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String resolveClientIp(ServerHttpRequest request, jakarta.servlet.http.HttpServletRequest http) {
        try {
            String ip = CommonUtil.getIPFromHttpRequest(http);
            if (StrUtil.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
                return ip.trim();
            }
        } catch (Exception ignored) {
        }
        InetSocketAddress remote = request.getRemoteAddress();
        if (remote != null && remote.getAddress() != null) {
            return remote.getAddress().getHostAddress();
        }
        return null;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }
}
