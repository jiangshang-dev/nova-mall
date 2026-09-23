package com.nova.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.nova.mall.base.LoginUser;
import com.nova.mall.entity.MallCsMessage;
import com.nova.mall.entity.MallCsSession;
import com.nova.mall.service.MallCsService;
import com.nova.mall.websocket.model.CsWsMessage;
import com.nova.mall.websocket.session.CsSessionHub;
import com.nova.mall.websocket.session.WsUserSession;
import com.nova.mall.websocket.spi.CsChatFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CsChatFacadeImpl implements CsChatFacade {
    private final MallCsService mallCsService;
    private final CsSessionHub hub;

    @Override
    public CsWsMessage onMemberConnected(WsUserSession ws, LoginUser user) {
        String name = StrUtil.blankToDefault(user.getUserRealName(), user.getUserName());
        MallCsSession session = mallCsService.openOrGetMemberSession(user.getId(), name, ws.getClientIp());
        ws.setCsSessionId(session.getSessionNo());
        hub.bindCsSession(session.getSessionNo(), user.getId(), session.getAgentId());

        List<MallCsMessage> history = mallCsService.history(session.getSessionNo(), 50);
        CsWsMessage hello = base("hello", session.getSessionNo());
        hello.setContent(session.getStatus() != null && session.getStatus() == 1
                ? "客服在线，可以开始咨询"
                : "已进入排队，客服接入后即可对话");
        Map<String, Object> data = new HashMap<>();
        data.put("session", session);
        data.put("history", history);
        hello.setData(data);

        // 通知在线客服有新排队
        if (session.getStatus() != null && session.getStatus() == 0) {
            CsWsMessage queue = base("queue", session.getSessionNo());
            queue.setContent("新会员排队：" + name);
            queue.setData(session);
            hub.broadcastAgents(queue);
        }
        return hello;
    }

    @Override
    public CsWsMessage onAgentConnected(WsUserSession ws, LoginUser user) {
        CsWsMessage hello = base("hello", null);
        hello.setContent("客服工作台已连接");
        Map<String, Object> data = new HashMap<>();
        data.put("waiting", mallCsService.waitingQueue());
        data.put("mine", mallCsService.agentSessions(user.getId()));
        hello.setData(data);
        return hello;
    }

    @Override
    public CsWsMessage onChat(WsUserSession ws, LoginUser user, CsWsMessage incoming) {
        String sessionNo = StrUtil.blankToDefault(incoming.getSessionId(), ws.getCsSessionId());
        if (StrUtil.isBlank(sessionNo)) {
            throw new IllegalArgumentException("缺少会话号");
        }
        String name = StrUtil.blankToDefault(user.getUserRealName(), user.getUserName());
        MallCsMessage saved = mallCsService.saveMessage(sessionNo, user.getId(), name, ws.getRole(), incoming.getContent());
        CsWsMessage out = base("chat", sessionNo);
        out.setMessageId(saved.getId());
        out.setContent(saved.getContent());
        out.setFromUserId(saved.getFromUserId());
        out.setFromUserName(saved.getFromUserName());
        out.setFromRole(saved.getFromRole());

        Integer memberId = hub.memberOf(sessionNo);
        Integer agentId = hub.agentOf(sessionNo);
        MallCsSession session = mallCsService.getByNo(sessionNo);
        if (memberId == null) memberId = session.getMemberId();
        if (agentId == null) agentId = session.getAgentId();

        if (memberId != null) hub.sendTo("member", memberId, out);
        if (agentId != null) hub.sendTo("agent", agentId, out);
        // 排队中会员消息也广播给客服，便于抢接
        if (agentId == null) {
            hub.broadcastAgents(out);
        }
        return out;
    }

    @Override
    public CsWsMessage onAssign(WsUserSession ws, LoginUser agent, String sessionId) {
        String name = StrUtil.blankToDefault(agent.getUserRealName(), agent.getUserName());
        MallCsSession session = mallCsService.assign(agent.getId(), name, sessionId);
        ws.setCsSessionId(session.getSessionNo());
        hub.bindCsSession(session.getSessionNo(), session.getMemberId(), agent.getId());

        CsWsMessage assigned = base("assign", session.getSessionNo());
        assigned.setContent("客服已接入");
        assigned.setData(session);
        hub.sendTo("member", session.getMemberId(), assigned);
        hub.sendTo("agent", agent.getId(), assigned);

        CsWsMessage tip = base("system", session.getSessionNo());
        tip.setContent("客服「" + name + "」已接入");
        tip.setFromRole("system");
        hub.sendTo("member", session.getMemberId(), tip);
        return assigned;
    }

    @Override
    public CsWsMessage onClose(WsUserSession ws, LoginUser user, String sessionId) {
        String sessionNo = StrUtil.blankToDefault(sessionId, ws.getCsSessionId());
        MallCsSession session = mallCsService.close(sessionNo, user.getId());
        CsWsMessage closed = base("close", session.getSessionNo());
        closed.setContent("会话已结束");
        closed.setData(session);
        hub.sendTo("member", session.getMemberId(), closed);
        if (session.getAgentId() != null) {
            hub.sendTo("agent", session.getAgentId(), closed);
        }
        hub.unbindCsSession(session.getSessionNo());
        return closed;
    }

    @Override
    public void onDisconnected(WsUserSession ws, LoginUser user) {
        // 保持会话状态，仅断开 WS；客服可稍后继续
    }

    private static CsWsMessage base(String type, String sessionNo) {
        CsWsMessage m = new CsWsMessage();
        m.setType(type);
        m.setSessionId(sessionNo);
        m.setTimestamp(System.currentTimeMillis());
        return m;
    }
}
