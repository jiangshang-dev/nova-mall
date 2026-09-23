package com.nova.mall.service;

import com.nova.mall.dto.LoginDTO;
import com.nova.mall.vo.LoginVO;

public interface AuthService {
    LoginVO login(LoginDTO dto);

    void logout();

    LoginVO info();

    void register(LoginDTO dto);
}
