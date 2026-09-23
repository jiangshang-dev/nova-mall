package com.nova.mall.service;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nova.mall.entity.SysOperLog;
import com.nova.mall.log.event.OperLogEvent;
public interface SysOperLogService extends IService<SysOperLog> {
  Page<SysOperLog> pageLogs(long pageNum, long pageSize, String title, String operName);
  void saveEvent(OperLogEvent event);
}
