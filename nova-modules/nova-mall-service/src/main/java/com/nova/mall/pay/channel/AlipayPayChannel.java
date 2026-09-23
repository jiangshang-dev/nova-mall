package com.nova.mall.pay.channel;

import com.nova.mall.pay.PaymentChannel;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * TODO: 支付宝支付渠道（证书/密钥就绪后实现）
 * 建议使用 alipay-sdk-java，策略接口已预留，接入时只需实现 create/handleNotify。
 */
@Component
public class AlipayPayChannel implements PaymentChannel {
    @Override
    public String code() {
        return "alipay";
    }

    @Override
    public String displayName() {
        return "支付宝";
    }

    @Override
    public boolean available() {
        return false;
    }

    @Override
    public String missingHint() {
        return "支付宝尚未接入：需配置 appId、应用私钥、支付宝公钥、notifyUrl（开放平台 open.alipay.com）";
    }

    @Override
    public PaymentCreateResult create(PaymentCreateRequest request) {
        return new PaymentCreateResult(true, missingHint() + " 当前走演示支付。", null, null, null);
    }

    @Override
    public PaymentNotifyResult handleNotify(String body, Map<String, String> headers) {
        return null;
    }
}
