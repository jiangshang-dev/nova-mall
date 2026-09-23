package com.nova.mall.websocket.session;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.nova.mall.websocket.model.CsWsMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 在线会话注册表：userId+role → ws，以及客服会话号 → 双方
 */
@Component
public class CsSessionHub {
    /** key: role:userId */
    private final Map<String, WsUserSession> online = new ConcurrentHashMap<>();
    /** key: csSessionId → member userId */
    private final Map<String, Integer> sessionMember = new ConcurrentHashMap<>();
    /** key: csSessionId → agent userId */
    private final Map<String, Integer> sessionAgent = new ConcurrentHashMap<>();

    private static String key(String role, Integer userId) {
        return role + ":" + userId;
    }

    public void bind(WsUserSession session) {
        online.put(key(session.getRole(), session.getUserId()), session);
    }

    public void unbind(String role, Integer userId) {
        if (userId == null) return;
        online.remove(key(role, userId));
    }

    public WsUserSession get(String role, Integer userId) {
        if (userId == null) return null;
        return online.get(key(role, userId));
    }

    public Collection<WsUserSession> onlineAgents() {
        return online.values().stream().filter(s -> "agent".equals(s.getRole())).toList();
    }

    public void bindCsSession(String csSessionId, Integer memberId, Integer agentId) {
        if (StrUtil.isBlank(csSessionId)) return;
        if (memberId != null) sessionMember.put(csSessionId, memberId);
        if (agentId != null) sessionAgent.put(csSessionId, agentId);
    }

    public void unbindCsSession(String csSessionId) {
        sessionMember.remove(csSessionId);
        sessionAgent.remove(csSessionId);
    }

    public Integer memberOf(String csSessionId) {
        return sessionMember.get(csSessionId);
    }

    public Integer agentOf(String csSessionId) {
        return sessionAgent.get(csSessionId);
    }

    public void sendTo(String role, Integer userId, CsWsMessage msg) {
        WsUserSession s = get(role, userId);
        if (s == null || s.getSocket() == null || !s.getSocket().isOpen()) return;
        send(s.getSocket(), msg);
    }

    public void broadcastAgents(CsWsMessage msg) {
        for (WsUserSession s : onlineAgents()) {
            send(s.getSocket(), msg);
        }
    }

    public void send(WebSocketSession socket, CsWsMessage msg) {
        if (socket == null || !socket.isOpen()) return;
        try {
            synchronized (socket) {
                socket.sendMessage(new TextMessage(JSONUtil.toJsonStr(msg)));
            }
        } catch (IOException ignored) {
            // ignore closed peers
        }
    }
}
