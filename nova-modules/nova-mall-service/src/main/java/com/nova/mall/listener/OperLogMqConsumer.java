package com.nova.mall.listener;
import com.nova.mall.log.event.OperLogEvent; import com.nova.mall.mq.constant.MqConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
@Slf4j @Component
@ConditionalOnProperty(prefix = "nova-mall.mq", name = "enabled", havingValue = "true")
public class OperLogMqConsumer {
  @RabbitListener(queues = MqConstants.QUEUE_OPER_LOG)
  public void onMq(OperLogEvent event) { log.debug("收到操作日志 MQ: {}", event == null ? null : event.getTitle()); }
}
