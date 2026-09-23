package com.nova.mall.listener;
import com.nova.mall.log.event.OperLogEvent; import com.nova.mall.service.SysOperLogService;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async; import org.springframework.stereotype.Component;
@Slf4j @Component @RequiredArgsConstructor
public class OperLogEventListener {
  private final SysOperLogService sysOperLogService;
  @Async @EventListener
  public void onEvent(OperLogEvent event) {
    try { sysOperLogService.saveEvent(event); } catch (Exception e) { log.error("保存操作日志失败", e); }
  }
}
