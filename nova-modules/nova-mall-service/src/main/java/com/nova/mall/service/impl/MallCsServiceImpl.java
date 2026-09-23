package com.nova.mall.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.entity.MallCsMessage;
import com.nova.mall.entity.MallCsSession;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.mapper.MallCsMessageMapper;
import com.nova.mall.mapper.MallCsSessionMapper;
import com.nova.mall.service.MallCsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MallCsServiceImpl extends ServiceImpl<MallCsSessionMapper, MallCsSession> implements MallCsService {
    private final MallCsMessageMapper messageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallCsSession openOrGetMemberSession(Integer memberId, String memberName) {
        return openOrGetMemberSession(memberId, memberName, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallCsSession openOrGetMemberSession(Integer memberId, String memberName, String clientIp) {
        MallCsSession open = this.getOne(new LambdaQueryWrapper<MallCsSession>()
                .eq(MallCsSession::getMemberId, memberId)
                .in(MallCsSession::getStatus, 0, 1)
                .orderByDesc(MallCsSession::getId)
                .last("LIMIT 1"));
        if (open != null) {
            if (StrUtil.isNotBlank(clientIp) && !clientIp.equals(open.getClientIp())) {
                open.setClientIp(clientIp);
                this.updateById(open);
            }
            return open;
        }
        MallCsSession s = new MallCsSession();
        s.setSessionNo("CS" + System.currentTimeMillis() + IdUtil.getSnowflakeNextIdStr().substring(12));
        s.setMemberId(memberId);
        s.setMemberName(memberName);
        s.setClientIp(StrUtil.blankToDefault(clientIp, null));
        s.setStatus(0);
        s.setLastMsg("会员已接入，等待客服...");
        s.setLastTime(System.currentTimeMillis());
        this.save(s);
        MallCsMessage tip = new MallCsMessage();
        tip.setSessionId(s.getId());
        tip.setSessionNo(s.getSessionNo());
        tip.setFromUserId(0);
        tip.setFromUserName("系统");
        tip.setFromRole("system");
        tip.setContent("您好，已为您接入人工客服，请稍候。");
        messageMapper.insert(tip);
        return s;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallCsSession assign(Integer agentId, String agentName, String sessionNo) {
        MallCsSession s = getByNo(sessionNo);
        if (s.getStatus() != null && s.getStatus() == 2) {
            throw new ServiceException("会话已结束");
        }
        if (s.getAgentId() != null && !s.getAgentId().equals(agentId) && s.getStatus() == 1) {
            throw new ServiceException("该会话已被其他客服接入");
        }
        s.setAgentId(agentId);
        s.setAgentName(agentName);
        s.setStatus(1);
        s.setLastMsg("客服已接入");
        s.setLastTime(System.currentTimeMillis());
        this.updateById(s);
        MallCsMessage tip = new MallCsMessage();
        tip.setSessionId(s.getId());
        tip.setSessionNo(s.getSessionNo());
        tip.setFromUserId(agentId);
        tip.setFromUserName(agentName);
        tip.setFromRole("system");
        tip.setContent("客服「" + agentName + "」已接入，请问有什么可以帮您？");
        messageMapper.insert(tip);
        return s;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallCsMessage saveMessage(String sessionNo, Integer fromUserId, String fromUserName, String fromRole, String content) {
        if (StrUtil.isBlank(content)) throw new ServiceException("消息不能为空");
        MallCsSession s = getByNo(sessionNo);
        if (s.getStatus() != null && s.getStatus() == 2) throw new ServiceException("会话已结束");
        MallCsMessage msg = new MallCsMessage();
        msg.setSessionId(s.getId());
        msg.setSessionNo(sessionNo);
        msg.setFromUserId(fromUserId);
        msg.setFromUserName(fromUserName);
        msg.setFromRole(fromRole);
        msg.setContent(content.trim());
        messageMapper.insert(msg);
        s.setLastMsg(content.trim().length() > 200 ? content.trim().substring(0, 200) : content.trim());
        s.setLastTime(System.currentTimeMillis());
        this.updateById(s);
        return msg;
    }

    @Override
    public MallCsSession close(String sessionNo, Integer operatorId) {
        MallCsSession s = getByNo(sessionNo);
        s.setStatus(2);
        s.setLastMsg("会话已结束");
        s.setLastTime(System.currentTimeMillis());
        this.updateById(s);
        return s;
    }

    @Override
    public List<MallCsSession> waitingQueue() {
        return this.list(new LambdaQueryWrapper<MallCsSession>()
                .eq(MallCsSession::getStatus, 0)
                .orderByAsc(MallCsSession::getId));
    }

    @Override
    public List<MallCsSession> agentSessions(Integer agentId) {
        return this.list(new LambdaQueryWrapper<MallCsSession>()
                .eq(MallCsSession::getAgentId, agentId)
                .eq(MallCsSession::getStatus, 1)
                .orderByDesc(MallCsSession::getLastTime));
    }

    @Override
    public List<MallCsMessage> history(String sessionNo, int limit) {
        MallCsSession s = getByNo(sessionNo);
        int lim = Math.min(Math.max(limit, 1), 200);
        return messageMapper.selectList(new LambdaQueryWrapper<MallCsMessage>()
                .eq(MallCsMessage::getSessionId, s.getId())
                .orderByAsc(MallCsMessage::getId)
                .last("LIMIT " + lim));
    }

    @Override
    public MallCsSession getByNo(String sessionNo) {
        MallCsSession s = this.getOne(new LambdaQueryWrapper<MallCsSession>()
                .eq(MallCsSession::getSessionNo, sessionNo).last("LIMIT 1"));
        if (s == null) throw new ServiceException("会话不存在");
        return s;
    }
}
