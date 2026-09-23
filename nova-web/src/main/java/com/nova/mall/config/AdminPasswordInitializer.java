package com.nova.mall.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nova.mall.entity.SysUser;
import com.nova.mall.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminPasswordInitializer implements ApplicationRunner {
    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        for (String name : new String[]{"admin", "user"}) {
            SysUser u = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, name).last("LIMIT 1"));
            if (u != null && !passwordEncoder.matches("admin123", u.getPassword())) {
                u.setPassword(passwordEncoder.encode("admin123"));
                sysUserMapper.updateById(u);
                log.info("已重置账号 {} 密码为 admin123", name);
            }
        }
    }
}
