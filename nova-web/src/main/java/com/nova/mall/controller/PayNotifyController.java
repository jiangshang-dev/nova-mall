package com.nova.mall.controller;

import cn.hutool.core.io.IoUtil;
import com.nova.mall.pay.PaymentChannel;
import com.nova.mall.pay.PaymentChannelRegistry;
import com.nova.mall.service.MallOrderService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付异步通知（必须公网可达）。微信/支付宝回调入口，后续渠道按 path 扩展。
 */
@Slf4j
@Hidden
@RestController
@AllArgsConstructor
@RequestMapping("/api/pay/notify")
public class PayNotifyController {
    private final PaymentChannelRegistry paymentChannelRegistry;
    private final MallOrderService mallOrderService;

    @PostMapping(value = "/wxpay", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> wxpay(HttpServletRequest request) throws Exception {
        String body = IoUtil.read(request.getInputStream(), StandardCharsets.UTF_8);
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String n = names.nextElement();
            headers.put(n, request.getHeader(n));
            // SDK 需要标准大小写键名
            if ("wechatpay-serial".equalsIgnoreCase(n)) headers.put("Wechatpay-Serial", request.getHeader(n));
            if ("wechatpay-nonce".equalsIgnoreCase(n)) headers.put("Wechatpay-Nonce", request.getHeader(n));
            if ("wechatpay-signature".equalsIgnoreCase(n)) headers.put("Wechatpay-Signature", request.getHeader(n));
            if ("wechatpay-timestamp".equalsIgnoreCase(n)) headers.put("Wechatpay-Timestamp", request.getHeader(n));
        }
        PaymentChannel channel = paymentChannelRegistry.get("wxpay");
        PaymentChannel.PaymentNotifyResult result = channel.handleNotify(body, headers);
        Map<String, String> resp = new HashMap<>();
        if (result == null) {
            resp.put("code", "FAIL");
            resp.put("message", "渠道未就绪");
            return resp;
        }
        if (result.success()) {
            try {
                mallOrderService.markPaidByNotify(result.orderNo(), result.tradeNo());
            } catch (Exception e) {
                log.warn("wx notify mark paid: {}", e.getMessage());
            }
            resp.put("code", "SUCCESS");
            resp.put("message", "成功");
            return resp;
        }
        resp.put("code", "FAIL");
        resp.put("message", "未支付成功");
        return resp;
    }

    /** TODO: 支付宝异步通知 */
    @PostMapping("/alipay")
    public String alipay() {
        return "fail";
    }
}
