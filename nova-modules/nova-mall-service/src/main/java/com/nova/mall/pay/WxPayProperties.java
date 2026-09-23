package com.nova.mall.pay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信 APIv3 配置。私钥等敏感项请放本地/密钥管理，勿提交仓库。
 */
@Data
@Component
@ConfigurationProperties(prefix = "nova-mall.pay.wxpay")
public class WxPayProperties {
    /** 是否尝试真实微信支付（仍需参数齐全） */
    private boolean enabled = false;
    private String mchId = "1668229108";
    /** 商户 API 证书序列号 */
    private String serialNo = "1A430A18195C5CCBE2EC31667945FE0749C2BDEB";
    /** 绑定的 AppID（公众号/小程序/开放平台） */
    private String appId;
    /** APIv3 密钥（32 位） */
    private String apiV3Key;
    /**
     * 商户私钥 PEM 内容，或以 classpath:/file: 路径指向 apiclient_key.pem
     * 例如：classpath:cert/apiclient_key.pem
     */
    private String privateKeyPath;
    /** 或直接粘贴 PEM 文本（优先于 path，生产不推荐） */
    private String privateKeyPem;
    /** 支付结果通知完整 URL，须公网 HTTPS */
    private String notifyUrl;
    /** Native=扫码（PC） JSAPI=公众号/小程序 H5=手机浏览器 */
    private String tradeType = "Native";
}
