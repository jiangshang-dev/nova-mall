package com.nova.mall.mq.producer;

import com.nova.mall.mq.constant.MqConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "nova-mall.mq", name = "enabled", havingValue = "true")
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;
    public void send(String routingKey, Object message) {
        try { rabbitTemplate.convertAndSend(MqConstants.EXCHANGE_NOVA, routingKey, message); }
        catch (Exception e) { log.error("MQ 发送失败 routingKey={}", routingKey, e); }
    }
    public void sendOperLog(Object message) { send(MqConstants.ROUTING_OPER_LOG, message); }
    public void sendGoodsSync(Object message) { send(MqConstants.ROUTING_GOODS_SYNC, message); }
}
