package com.nova.mall.websocket.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.nova.mall.base.LoginUser;
import com.nova.mall.websocket.interceptor.WsAuthHandshakeInterceptor;
import com.nova.mall.websocket.model.CsWsMessage;
import com.nova.mall.websocket.session.CsSessionHub;
import com.nova.mall.websocket.session.WsUserSession;
import com.nova.mall.websocket.spi.CsChatFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsWebSocketHandler extends TextWebSocketHandler {
    private final CsSessionHub hub;
    private final CsChatFacade csChatFacade;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        LoginUser user = (LoginUser) session.getAttributes().get(WsAuthHandshakeInterceptor.ATTR_USER);
        String role = String.valueOf(session.getAttributes().get(WsAuthHandshakeInterceptor.ATTR_ROLE));
        WsUserSession ws = WsUserSession.of(session, user, role);
        hub.bind(ws);
        CsWsMessage hello = "agent".equals(role)
                ? csChatFacade.onAgentConnected(ws, user)
                : csChatFacade.onMemberConnected(ws, user);
        if (hello != null) {
            hub.send(session, hello);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        LoginUser user = (LoginUser) session.getAttributes().get(WsAuthHandshakeInterceptor.ATTR_USER);
        String role = String.valueOf(session.getAttributes().get(WsAuthHandshakeInterceptor.ATTR_ROLE));
        WsUserSession ws = hub.get(role, user.getId());
        if (ws == null) {
            ws = WsUserSession.of(session, user, role);
            hub.bind(ws);
        }
        CsWsMessage incoming;
        try {
            incoming = JSONUtil.toBean(message.getPayload(), CsWsMessage.class);
        } catch (Exception e) {
            hub.send(session, error("消息格式错误"));
            return;
        }
        if (incoming == null || StrUtil.isBlank(incoming.getType())) {
            hub.send(session, error("缺少 type"));
            return;
        }
        String type = incoming.getType().toLowerCase();
        try {
            switch (type) {
                case "ping" -> {
                    CsWsMessage pong = new CsWsMessage();
                    pong.setType("pong");
                    pong.setTimestamp(System.currentTimeMillis());
                    hub.send(session, pong);
                }
                case "chat" -> {
                    CsWsMessage out = csChatFacade.onChat(ws, user, incoming);
                    if (out != null) {
                        // 已由 facade 推送给双方，也可回执自己
                    }
                }
                case "assign" -> {
                    if (!"agent".equals(role)) {
                        hub.send(session, error("仅客服可接入会话"));
                        return;
                    }
                    CsWsMessage out = csChatFacade.onAssign(ws, user, incoming.getSessionId());
                    if (out != null) hub.send(session, out);
                }
                case "close" -> {
                    CsWsMessage out = csChatFacade.onClose(ws, user, incoming.getSessionId());
                    if (out != null) hub.send(session, out);
                }
                default -> hub.send(session, error("不支持的消息类型: " + type));
            }
        } catch (Exception e) {
            log.warn("cs ws handle error: {}", e.getMessage());
            hub.send(session, error(StrUtil.blankToDefault(e.getMessage(), "处理失败")));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        LoginUser user = (LoginUser) session.getAttributes().get(WsAuthHandshakeInterceptor.ATTR_USER);
        String role = String.valueOf(session.getAttributes().get(WsAuthHandshakeInterceptor.ATTR_ROLE));
        if (user != null) {
            WsUserSession ws = hub.get(role, user.getId());
            hub.unbind(role, user.getId());
            if (ws != null) {
                try {
                    csChatFacade.onDisconnected(ws, user);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static CsWsMessage error(String msg) {
        CsWsMessage m = new CsWsMessage();
        m.setType("error");
        m.setContent(msg);
        m.setTimestamp(System.currentTimeMillis());
        return m;
    }
}
