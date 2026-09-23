package com.nova.mall.pay.channel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.nova.mall.pay.PaymentChannel;
import com.nova.mall.pay.WxPayProperties;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 微信支付（APIv3 Native 扫码）。配置不全或初始化失败时 available=false，上层走演示支付。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WechatPayChannel implements PaymentChannel {
    private final WxPayProperties props;
    private final ResourceLoader resourceLoader;

    private volatile Config config;
    private volatile NativePayService nativePayService;
    private volatile NotificationParser notificationParser;
    /** 初始化失败原因（展示给收银台） */
    private volatile String initError;

    @PostConstruct
    public void init() {
        this.initError = null;
        if (!props.isEnabled()) {
            log.info("[wxpay] enabled=false，使用演示支付");
            return;
        }
        List<String> missing = missingList();
        if (!missing.isEmpty()) {
            this.initError = "配置不完整：" + String.join("、", missing);
            log.warn("[wxpay] {}", initError);
            return;
        }
        try {
            String certHint = checkMerchantCertPair();
            if (StrUtil.isNotBlank(certHint)) {
                this.initError = certHint;
                log.error("[wxpay] {}", certHint);
                return;
            }
            String privateKey = loadPrivateKey();
            RSAAutoCertificateConfig rsaConfig = new RSAAutoCertificateConfig.Builder()
                    .merchantId(props.getMchId())
                    .privateKey(privateKey)
                    .merchantSerialNumber(props.getSerialNo())
                    .apiV3Key(props.getApiV3Key())
                    .build();
            this.config = rsaConfig;
            this.nativePayService = new NativePayService.Builder().config(rsaConfig).build();
            this.notificationParser = new NotificationParser(rsaConfig);
            log.info("[wxpay] 初始化成功 mchId={}", props.getMchId());
        } catch (Exception e) {
            this.initError = "SDK 初始化失败：" + e.getMessage()
                    + "（请确认 apiclient_key.pem 与 serial-no 同属商户 " + props.getMchId() + "）";
            log.error("[wxpay] 初始化失败: {}", e.getMessage(), e);
            this.config = null;
            this.nativePayService = null;
            this.notificationParser = null;
        }
    }

    @Override
    public String code() {
        return "wxpay";
    }

    @Override
    public String displayName() {
        return "微信支付";
    }

    @Override
    public boolean available() {
        return props.isEnabled() && nativePayService != null && missingList().isEmpty() && StrUtil.isBlank(initError);
    }

    @Override
    public String missingHint() {
        List<String> m = missingList();
        if (!props.isEnabled()) {
            return "请将 nova-mall.pay.wxpay.enabled 设为 true";
        }
        if (!m.isEmpty()) {
            return "微信支付缺少：" + String.join("、", m)
                    + "。获取位置见商户平台 pay.weixin.qq.com → 账户中心 → API安全 / 产品中心。";
        }
        if (StrUtil.isNotBlank(initError)) {
            return initError;
        }
        if (nativePayService == null) {
            return "微信支付客户端未就绪，请查看启动日志 [wxpay]";
        }
        return "";
    }

    private List<String> missingList() {
        List<String> m = new ArrayList<>();
        if (StrUtil.isBlank(props.getMchId())) m.add("商户号 mchId");
        if (StrUtil.isBlank(props.getSerialNo())) m.add("证书序列号 serialNo");
        if (StrUtil.isBlank(props.getAppId())) m.add("AppID appId");
        if (StrUtil.isBlank(props.getApiV3Key())) m.add("APIv3密钥 apiV3Key");
        if (StrUtil.isBlank(props.getPrivateKeyPath()) && StrUtil.isBlank(props.getPrivateKeyPem())) {
            m.add("商户私钥 privateKeyPath/privateKeyPem");
        }
        if (StrUtil.isBlank(props.getNotifyUrl())) {
            m.add("回调地址 notifyUrl");
        }
        return m;
    }

    private String notifyWarn() {
        if (StrUtil.isNotBlank(props.getNotifyUrl()) && isPlaceholderNotify(props.getNotifyUrl())) {
            return "（提醒：notify-url 仍是 xxx 占位，扫码后支付成功回调可能收不到，请换成 natapp 真实域名）";
        }
        return "";
    }

    private static boolean isPlaceholderNotify(String url) {
        String u = url.toLowerCase(Locale.ROOT);
        return u.contains("xxx.natapp") || u.contains("your.domain") || u.contains("example.com");
    }

    /**
     * 校验本地 apiclient_cert.pem 是否与配置的商户号/序列号一致（避免用培训课别人的证书）。
     */
    private String checkMerchantCertPair() {
        try {
            Resource certRes = resourceLoader.getResource("classpath:cert/apiclient_cert.pem");
            if (!certRes.exists()) {
                return null; // 仅有 key 时跳过，交给微信侧校验
            }
            String pem = certRes.getContentAsString(StandardCharsets.UTF_8);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(
                    new ByteArrayInputStream(pem.getBytes(StandardCharsets.UTF_8)));
            String fileSerial = toHexSerial(cert.getSerialNumber());
            String cfgSerial = normalizeHex(props.getSerialNo());
            String subject = cert.getSubjectX500Principal().getName();
            if (!fileSerial.equalsIgnoreCase(cfgSerial)) {
                return "商户API证书不匹配：本地 apiclient_cert.pem 序列号=" + fileSerial
                        + "，配置 serial-no=" + cfgSerial
                        + "；证书主体=" + subject
                        + "。请到 pay.weixin.qq.com 用商户号 " + props.getMchId()
                        + " 重新下载 API 证书，覆盖 cert/apiclient_key.pem 与 apiclient_cert.pem，并把 serial-no 改成新证书序列号。";
            }
            if (StrUtil.isNotBlank(props.getMchId()) && subject != null && subject.contains("CN=")
                    && !subject.contains(props.getMchId())) {
                return "商户API证书不属于商户号 " + props.getMchId() + "（证书主体：" + subject
                        + "）。请替换为该商户自己的证书文件。";
            }
            return null;
        } catch (Exception e) {
            log.warn("[wxpay] 读取本地证书失败: {}", e.getMessage());
            return null;
        }
    }

    private static String toHexSerial(BigInteger serial) {
        return normalizeHex(serial.toString(16));
    }

    private static String normalizeHex(String hex) {
        if (hex == null) return "";
        String s = hex.trim().replace(":", "").toUpperCase(Locale.ROOT);
        // 去掉前导 0 再比，但微信序列号一般固定长度，保留原串比较更稳；两端都大写去冒号即可
        return s;
    }

    @Override
    public PaymentCreateResult create(PaymentCreateRequest request) {
        if (!available()) {
            String demoCode = "weixin://wxpay/bizpayurl?pr=DEMO" + request.orderNo();
            String tip = "仍为演示码，原因：" + StrUtil.blankToDefault(missingHint(), "渠道未就绪");
            return new PaymentCreateResult(true, tip, demoCode, null, null);
        }
        try {
            PrepayRequest prepay = new PrepayRequest();
            prepay.setAppid(props.getAppId());
            prepay.setMchid(props.getMchId());
            prepay.setDescription(StrUtil.blankToDefault(request.description(), "Nova Mall 订单"));
            prepay.setOutTradeNo(request.orderNo());
            prepay.setNotifyUrl(props.getNotifyUrl());
            Amount amount = new Amount();
            amount.setTotal(toFen(request.amount()));
            prepay.setAmount(amount);
            PrepayResponse resp = nativePayService.prepay(prepay);
            return new PaymentCreateResult(false,
                    "请使用微信扫一扫完成支付" + notifyWarn(),
                    resp.getCodeUrl(), null, null);
        } catch (Exception e) {
            log.error("[wxpay] 下单失败", e);
            String demoCode = "weixin://wxpay/bizpayurl?pr=DEMO" + request.orderNo();
            return new PaymentCreateResult(true,
                    "微信下单失败：" + e.getMessage() + "，已回退演示扫码",
                    demoCode, null, null);
        }
    }

    @Override
    public PaymentNotifyResult handleNotify(String body, Map<String, String> headers) {
        if (notificationParser == null) {
            return null;
        }
        RequestParam param = new RequestParam.Builder()
                .serialNumber(headers.get("Wechatpay-Serial"))
                .nonce(headers.get("Wechatpay-Nonce"))
                .signature(headers.get("Wechatpay-Signature"))
                .timestamp(headers.get("Wechatpay-Timestamp"))
                .body(body)
                .build();
        Transaction tx = notificationParser.parse(param, Transaction.class);
        boolean ok = tx.getTradeState() == Transaction.TradeStateEnum.SUCCESS;
        String orderNo = tx.getOutTradeNo();
        String tradeNo = tx.getTransactionId();
        BigDecimal amount = null;
        if (tx.getAmount() != null && tx.getAmount().getTotal() != null) {
            amount = BigDecimal.valueOf(tx.getAmount().getTotal()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return new PaymentNotifyResult(ok, orderNo, tradeNo, amount, body);
    }

    private String loadPrivateKey() throws Exception {
        if (StrUtil.isNotBlank(props.getPrivateKeyPem())) {
            return props.getPrivateKeyPem().trim();
        }
        String path = props.getPrivateKeyPath();
        Resource resource = resourceLoader.getResource(path);
        if (resource.exists()) {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        }
        if (FileUtil.exist(path)) {
            return FileUtil.readString(path, StandardCharsets.UTF_8);
        }
        throw new IllegalStateException("找不到商户私钥文件: " + path);
    }

    private static int toFen(BigDecimal yuan) {
        if (yuan == null) return 0;
        return yuan.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValueExact();
    }
}
