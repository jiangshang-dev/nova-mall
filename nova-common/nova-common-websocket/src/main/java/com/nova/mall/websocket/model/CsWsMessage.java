package com.nova.mall.websocket.model;

import lombok.Data;

/**
 * 客服 WebSocket 报文
 * type: hello / chat / close / ping / pong / system / error / queue / assign
 */
@Data
public class CsWsMessage {
    private String type;
    private String sessionId;
    private String content;
    private Long messageId;
    private Integer fromUserId;
    private String fromUserName;
    /** member / agent / system */
    private String fromRole;
    private Long timestamp;
    private Object data;
}
