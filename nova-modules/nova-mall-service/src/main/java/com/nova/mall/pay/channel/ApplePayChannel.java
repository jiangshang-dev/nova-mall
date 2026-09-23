package com.nova.mall.pay.channel;

import com.nova.mall.pay.PaymentChannel;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * TODO: Apple Pay（需 Apple Merchant ID、Payment Processing Certificate、后端校验 payment token，
 * 国内一般经微信/支付宝或第三方聚合；可后续在此渠道内对接）。
 */
@Component
public class ApplePayChannel implements PaymentChannel {
    @Override
    public String code() {
        return "applepay";
    }

    @Override
    public String displayName() {
        return "Apple Pay";
    }

    @Override
    public boolean available() {
        return false;
    }

    @Override
    public String missingHint() {
        return "Apple Pay 尚未接入：需 Apple Developer Merchant ID 与支付处理证书";
    }

    @Override
    public PaymentCreateResult create(PaymentCreateRequest request) {
        return new PaymentCreateResult(true, missingHint() + " 当前不可用。", null, null, null);
    }

    @Override
    public PaymentNotifyResult handleNotify(String body, Map<String, String> headers) {
        return null;
    }
}
