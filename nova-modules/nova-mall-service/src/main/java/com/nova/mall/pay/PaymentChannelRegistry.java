package com.nova.mall.pay;

import com.nova.mall.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付渠道注册表（策略 + 简单工厂）
 */
@Component
public class PaymentChannelRegistry {
    private final Map<String, PaymentChannel> channels = new HashMap<>();

    public PaymentChannelRegistry(List<PaymentChannel> list) {
        for (PaymentChannel c : list) {
            channels.put(c.code().toLowerCase(), c);
        }
    }

    public PaymentChannel get(String code) {
        if (code == null) throw new ServiceException("请选择支付方式");
        PaymentChannel c = channels.get(code.toLowerCase());
        if (c == null) throw new ServiceException("不支持的支付方式: " + code);
        return c;
    }

    public Map<String, PaymentChannel> all() {
        return Map.copyOf(channels);
    }
}
