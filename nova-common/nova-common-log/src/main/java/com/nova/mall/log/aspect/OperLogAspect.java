package com.nova.mall.log.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.nova.mall.base.LoginUser;
import com.nova.mall.log.annotation.OperLog;
import com.nova.mall.log.event.OperLogEvent;
import com.nova.mall.mq.constant.MqConstants;
import com.nova.mall.mq.producer.MessageProducer;
import com.nova.mall.util.CommonUtil;
import com.nova.mall.utils.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Order(2)
@Component
public class OperLogAspect {
    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();
    private final ApplicationEventPublisher eventPublisher;
    private MessageProducer messageProducer;

    public OperLogAspect(ApplicationEventPublisher eventPublisher) { this.eventPublisher = eventPublisher; }
    @Autowired(required = false)
    public void setMessageProducer(MessageProducer messageProducer) { this.messageProducer = messageProducer; }

    @Before("@annotation(com.nova.mall.log.annotation.OperLog)")
    public void before() { START_TIME.set(System.currentTimeMillis()); }

    @AfterReturning(pointcut = "@annotation(operLog)", returning = "result")
    public void afterReturning(JoinPoint joinPoint, OperLog operLog, Object result) {
        publish(joinPoint, operLog, result, null, 1);
    }

    @AfterThrowing(pointcut = "@annotation(operLog)", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, OperLog operLog, Exception ex) {
        publish(joinPoint, operLog, null, ex, 0);
    }

    private void publish(JoinPoint joinPoint, OperLog operLog, Object result, Exception ex, int status) {
        try {
            long cost = 0L;
            Long start = START_TIME.get();
            if (start != null) cost = System.currentTimeMillis() - start;
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            HttpServletRequest request = currentRequest();
            LoginUser user = UserUtil.getUser();
            OperLogEvent event = OperLogEvent.builder()
                    .title(operLog.title())
                    .businessType(OperLogEvent.typeCode(operLog.businessType()))
                    .method(method.getDeclaringClass().getName() + "." + method.getName())
                    .requestMethod(request == null ? null : request.getMethod())
                    .operName(user == null ? null : user.getUserName())
                    .operUrl(request == null ? null : request.getRequestURI())
                    .operIp(request == null ? null : CommonUtil.getIPFromHttpRequest(request))
                    .operParam(StrUtil.maxLength(JSONUtil.toJsonStr(joinPoint.getArgs()), 2000))
                    .jsonResult(result == null ? null : StrUtil.maxLength(JSONUtil.toJsonStr(result), 2000))
                    .status(status)
                    .errorMsg(ex == null ? null : StrUtil.maxLength(ex.getMessage(), 500))
                    .costTime(cost)
                    .operTime(System.currentTimeMillis())
                    .build();
            eventPublisher.publishEvent(event);
            if (messageProducer != null) messageProducer.send(MqConstants.ROUTING_OPER_LOG, event);
        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        } finally {
            START_TIME.remove();
        }
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs == null ? null : attrs.getRequest();
    }
}
