package com.nova.mall.service;

import com.nova.mall.entity.MallCsMessage;
import com.nova.mall.entity.MallCsSession;

import java.util.List;

public interface MallCsService {
    MallCsSession openOrGetMemberSession(Integer memberId, String memberName);

    MallCsSession openOrGetMemberSession(Integer memberId, String memberName, String clientIp);

    MallCsSession assign(Integer agentId, String agentName, String sessionNo);

    MallCsMessage saveMessage(String sessionNo, Integer fromUserId, String fromUserName, String fromRole, String content);

    MallCsSession close(String sessionNo, Integer operatorId);

    List<MallCsSession> waitingQueue();

    List<MallCsSession> agentSessions(Integer agentId);

    List<MallCsMessage> history(String sessionNo, int limit);

    MallCsSession getByNo(String sessionNo);
}
