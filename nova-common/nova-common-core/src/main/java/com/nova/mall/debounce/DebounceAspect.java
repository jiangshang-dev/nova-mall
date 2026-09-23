package com.nova.mall.debounce;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.nova.mall.debounce.annotation.Debounce;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.utils.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/** 接口防重复提交切面 */
@Aspect
@Order(1)
@Component
@RequiredArgsConstructor
public class DebounceAspect {
    private static final String KEY_PREFIX = "nova:debounce:";
    private final StringRedisTemplate stringRedisTemplate;

    @Around("@annotation(com.nova.mall.debounce.annotation.Debounce)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Debounce debounce = method.getAnnotation(Debounce.class);
        if (debounce == null) return joinPoint.proceed();
        Integer userId = UserUtil.getUser() == null ? null : UserUtil.getUser().getId();
        String uri = "";
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) uri = attrs.getRequest().getRequestURI();
        String key = KEY_PREFIX + DigestUtil.md5Hex(StrUtil.format("{}:{}:{}:{}", userId, uri, method.getDeclaringClass().getName(), method.getName()));
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", debounce.expire(), TimeUnit.MILLISECONDS);
        if (Boolean.FALSE.equals(success)) throw new ServiceException(429, "操作过于频繁，请稍后再试");
        return joinPoint.proceed();
    }
}
