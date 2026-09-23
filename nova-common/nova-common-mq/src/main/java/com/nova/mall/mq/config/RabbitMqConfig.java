package com.nova.mall.mq.config;

import com.nova.mall.mq.constant.MqConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "nova-mall.mq", name = "enabled", havingValue = "true")
public class RabbitMqConfig {
    @Bean public MessageConverter jacksonMessageConverter() { return new Jackson2JsonMessageConverter(); }
    @Bean public DirectExchange novaExchange() { return new DirectExchange(MqConstants.EXCHANGE_NOVA, true, false); }
    @Bean public Queue operLogQueue() { return new Queue(MqConstants.QUEUE_OPER_LOG, true); }
    @Bean public Queue goodsSyncQueue() { return new Queue(MqConstants.QUEUE_GOODS_SYNC, true); }
    @Bean public Binding operLogBinding(Queue operLogQueue, DirectExchange novaExchange) {
        return BindingBuilder.bind(operLogQueue).to(novaExchange).with(MqConstants.ROUTING_OPER_LOG);
    }
    @Bean public Binding goodsSyncBinding(Queue goodsSyncQueue, DirectExchange novaExchange) {
        return BindingBuilder.bind(goodsSyncQueue).to(novaExchange).with(MqConstants.ROUTING_GOODS_SYNC);
    }
}
