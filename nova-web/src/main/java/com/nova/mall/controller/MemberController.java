package com.nova.mall.controller;

import cn.hutool.core.util.StrUtil;
import com.nova.mall.base.LoginUser;
import com.nova.mall.debounce.annotation.Debounce;
import com.nova.mall.dto.MemberProfileDTO;
import com.nova.mall.entity.SysUser;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.log.annotation.OperLog;
import com.nova.mall.log.enums.BusinessType;
import com.nova.mall.mapper.SysUserMapper;
import com.nova.mall.utils.R;
import com.nova.mall.utils.UserUtil;
import com.nova.mall.vo.MemberProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "会员中心")
@RestController
@AllArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final SysUserMapper sysUserMapper;

    @Operation(summary = "我的资料")
    @GetMapping("/profile")
    public R<MemberProfileVO> profile() {
        return R.ok(loadProfile());
    }

    @Operation(summary = "更新昵称/头像")
    @Debounce
    @OperLog(title = "会员资料", businessType = BusinessType.UPDATE)
    @PutMapping("/profile")
    public R<MemberProfileVO> update(@Valid @RequestBody MemberProfileDTO dto) {
        LoginUser login = UserUtil.getUser();
        if (login == null) throw new ServiceException(401, "请先登录");
        SysUser user = sysUserMapper.selectById(login.getId());
        if (user == null) throw new ServiceException("用户不存在");
        user.setUserRealName(dto.getUserRealName().trim());
        if (dto.getAvatar() != null) {
            user.setAvatar(StrUtil.blankToDefault(dto.getAvatar(), null));
        }
        sysUserMapper.updateById(user);
        return R.ok(loadProfile());
    }

    private MemberProfileVO loadProfile() {
        LoginUser login = UserUtil.getUser();
        if (login == null) throw new ServiceException(401, "请先登录");
        SysUser user = sysUserMapper.selectById(login.getId());
        if (user == null) throw new ServiceException("用户不存在");
        MemberProfileVO vo = new MemberProfileVO();
        vo.setUserId(user.getId());
        vo.setUserName(user.getUserName());
        vo.setUserRealName(user.getUserRealName());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        return vo;
    }
}
