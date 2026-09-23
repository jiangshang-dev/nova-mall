package com.nova.mall.pay;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付渠道策略：微信 / 支付宝 / Apple Pay 等统一入口。
 */
public interface PaymentChannel {

    /** wxpay / alipay / applepay */
    String code();

    String displayName();

    /** 商户参数是否齐全，可发起真实支付 */
    boolean available();

    /** 缺什么配置（给人看） */
    String missingHint();

    PaymentCreateResult create(PaymentCreateRequest request);

    /**
     * 异步通知验签与解析；不支持时返回 null。
     */
    PaymentNotifyResult handleNotify(String body, Map<String, String> headers);

    record PaymentCreateRequest(
            String orderNo,
            BigDecimal amount,
            String description,
            String clientIp,
            String openId
    ) {}

    record PaymentCreateResult(
            boolean demoMode,
            String tip,
            /** Native 扫码支付链接 */
            String codeUrl,
            /** H5/JSAPI 等跳转或调起参数（JSON） */
            String payPayload,
            String prepayId
    ) {}

    record PaymentNotifyResult(
            boolean success,
            String orderNo,
            String tradeNo,
            BigDecimal amount,
            String raw
    ) {}
}
