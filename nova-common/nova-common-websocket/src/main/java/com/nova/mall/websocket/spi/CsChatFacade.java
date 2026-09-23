package com.nova.mall.websocket.spi;

import com.nova.mall.base.LoginUser;
import com.nova.mall.websocket.model.CsWsMessage;
import com.nova.mall.websocket.session.WsUserSession;

/**
 * 客服业务由业务模块实现，WebSocket 模块只负责连接与推送。
 */
public interface CsChatFacade {

    /** 会员上线：打开/复用会话 */
    CsWsMessage onMemberConnected(WsUserSession ws, LoginUser user);

    /** 客服上线 */
    CsWsMessage onAgentConnected(WsUserSession ws, LoginUser user);

    /** 聊天消息 */
    CsWsMessage onChat(WsUserSession ws, LoginUser user, CsWsMessage incoming);

    /** 客服接入会话 */
    CsWsMessage onAssign(WsUserSession ws, LoginUser agent, String sessionId);

    /** 结束会话 */
    CsWsMessage onClose(WsUserSession ws, LoginUser user, String sessionId);

    /** 下线清理 */
    void onDisconnected(WsUserSession ws, LoginUser user);
}
