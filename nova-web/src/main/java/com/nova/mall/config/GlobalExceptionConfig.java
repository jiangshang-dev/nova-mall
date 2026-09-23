package com.nova.mall.config;

import cn.hutool.core.util.StrUtil;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.utils.R;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionConfig {
    @ExceptionHandler(AccessDeniedException.class)
    public R<Void> handleAccessDenied(AccessDeniedException e) {
        return R.fail(403, "无权限访问");
    }

    @ExceptionHandler(AuthenticationException.class)
    public R<Void> handleAuth(AuthenticationException e) {
        return R.fail(401, StrUtil.blankToDefault(e.getMessage(), "未登录或登录已失效"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleMethod(HttpRequestMethodNotSupportedException e) {
        return R.fail(405, "请求方法不支持");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public R<Void> handleMedia(HttpMediaTypeNotSupportedException e) {
        return R.fail(415, "Content-Type 不支持");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public R<Void> handle404(NoHandlerFoundException e) {
        return R.fail(404, "接口不存在");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<Void> handleMissing(MissingServletRequestParameterException e) {
        return R.fail(400, "缺少请求参数: " + e.getParameterName());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public R<Void> handleResolve(Exception e) {
        return R.fail(400, "参数解析失败");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining("; "));
        return R.fail(400, msg);
    }

    @ExceptionHandler(BindException.class)
    public R<Map<String, Object>> handleBind(BindException e) {
        Map<String, Object> map = e.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));
        return R.fail(400, map, "参数校验失败");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public R<Void> handleConstraint(ConstraintViolationException e) {
        return R.fail(400, e.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    public R<Object> handleService(ServiceException e) {
        return R.fail(e.getCode(), e.getData(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return R.fail(500, "服务器内部错误");
    }
}
