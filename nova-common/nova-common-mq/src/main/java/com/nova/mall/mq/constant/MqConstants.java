package com.nova.mall.mq.constant;
public final class MqConstants {
    private MqConstants() {}
    public static final String EXCHANGE_NOVA = "nova.exchange";
    public static final String ROUTING_OPER_LOG = "nova.oper.log";
    public static final String QUEUE_OPER_LOG = "nova.oper.log.queue";
    public static final String ROUTING_GOODS_SYNC = "nova.goods.sync";
    public static final String QUEUE_GOODS_SYNC = "nova.goods.sync.queue";
}
