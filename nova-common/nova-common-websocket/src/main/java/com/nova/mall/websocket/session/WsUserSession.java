package com.nova.mall.websocket.session;

import com.nova.mall.base.LoginUser;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
public class WsUserSession {
    private String wsSessionId;
    private Integer userId;
    private String userName;
    /** member / agent */
    private String role;
    private String csSessionId;
    private String clientIp;
    private WebSocketSession socket;

    public static WsUserSession of(WebSocketSession socket, LoginUser user, String role) {
        WsUserSession s = new WsUserSession();
        s.setWsSessionId(socket.getId());
        s.setSocket(socket);
        s.setUserId(user.getId());
        s.setUserName(user.getUserRealName() != null ? user.getUserRealName() : user.getUserName());
        s.setRole(role);
        Object ip = socket.getAttributes().get(com.nova.mall.websocket.interceptor.WsAuthHandshakeInterceptor.ATTR_CLIENT_IP);
        if (ip != null) s.setClientIp(String.valueOf(ip));
        return s;
    }
}
