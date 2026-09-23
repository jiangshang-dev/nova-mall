package com.nova.mall.websocket.config;

import com.nova.mall.websocket.handler.CsWebSocketHandler;
import com.nova.mall.websocket.interceptor.WsAuthHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final CsWebSocketHandler csWebSocketHandler;
    private final WsAuthHandshakeInterceptor wsAuthHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(csWebSocketHandler, "/ws/cs")
                .addInterceptors(wsAuthHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }
}
