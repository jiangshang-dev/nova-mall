package com.nova.mall.controller;

import com.nova.mall.debounce.annotation.Debounce;
import com.nova.mall.dto.LoginDTO;
import com.nova.mall.log.annotation.OperLog;
import com.nova.mall.log.enums.BusinessType;
import com.nova.mall.service.AuthService;
import com.nova.mall.utils.R;
import com.nova.mall.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理")
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return R.ok(authService.login(dto));
    }

    @Operation(summary = "退出登录")
    @Debounce
    @OperLog(title = "退出登录", businessType = BusinessType.OTHER)
    @PostMapping("/logout")
    public R<Void> logout() {
        authService.logout();
        return R.ok();
    }

    @Operation(summary = "当前用户信息")
    @GetMapping("/info")
    public R<LoginVO> info() {
        return R.ok(authService.info());
    }

    @Operation(summary = "C端注册")
    @Debounce
    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody LoginDTO dto) {
        authService.register(dto);
        return R.ok();
    }
}
