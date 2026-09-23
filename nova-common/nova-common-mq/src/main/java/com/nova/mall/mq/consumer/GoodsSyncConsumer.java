package com.nova.mall.mq.consumer;

import com.nova.mall.mq.constant.MqConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "nova-mall.mq", name = "enabled", havingValue = "true")
public class GoodsSyncConsumer {
    @RabbitListener(queues = MqConstants.QUEUE_GOODS_SYNC)
    public void onMessage(String message) { log.info("收到商品同步消息(占位): {}", message); }
}
