package com.nova.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nova.mall.entity.SysOperLog;
import com.nova.mall.log.event.OperLogEvent;
import com.nova.mall.mapper.SysOperLogMapper;
import com.nova.mall.service.SysOperLogService;
import org.springframework.stereotype.Service;

@Service
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {
    @Override
    public Page<SysOperLog> pageLogs(long pageNum, long pageSize, String title, String operName) {
        return this.page(new Page<>(pageNum, pageSize), new LambdaQueryWrapper<SysOperLog>()
                .like(StrUtil.isNotBlank(title), SysOperLog::getTitle, title)
                .like(StrUtil.isNotBlank(operName), SysOperLog::getOperName, operName)
                .orderByDesc(SysOperLog::getOperTime));
    }

    @Override
    public void saveEvent(OperLogEvent event) {
        if (event == null) return;
        SysOperLog log = new SysOperLog();
        log.setTitle(event.getTitle());
        log.setBusinessType(event.getBusinessType());
        log.setMethod(event.getMethod());
        log.setRequestMethod(event.getRequestMethod());
        log.setOperName(event.getOperName());
        log.setOperUrl(event.getOperUrl());
        log.setOperIp(event.getOperIp());
        log.setOperParam(event.getOperParam());
        log.setJsonResult(event.getJsonResult());
        log.setStatus(event.getStatus());
        log.setErrorMsg(event.getErrorMsg());
        log.setCostTime(event.getCostTime());
        log.setOperTime(event.getOperTime());
        this.save(log);
    }
}
