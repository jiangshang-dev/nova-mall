package com.nova.mall.controller;

import com.nova.mall.base.LoginUser;
import com.nova.mall.entity.MallCsMessage;
import com.nova.mall.entity.MallCsSession;
import com.nova.mall.exception.ServiceException;
import com.nova.mall.service.MallCsService;
import com.nova.mall.util.CommonUtil;
import com.nova.mall.utils.R;
import com.nova.mall.utils.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "人工客服")
@RestController
@AllArgsConstructor
@RequestMapping("/api/cs")
public class MallCsController {
    private final MallCsService mallCsService;

    @Operation(summary = "会员打开/获取会话")
    @PostMapping("/session/open")
    public R<MallCsSession> open(HttpServletRequest request) {
        LoginUser user = requireLogin();
        String name = user.getUserRealName() != null ? user.getUserRealName() : user.getUserName();
        String clientIp = CommonUtil.getIPFromHttpRequest(request);
        return R.ok(mallCsService.openOrGetMemberSession(user.getId(), name, clientIp));
    }

    @Operation(summary = "会话历史消息")
    @GetMapping("/session/{sessionNo}/messages")
    public R<List<MallCsMessage>> messages(@PathVariable String sessionNo,
                                           @RequestParam(defaultValue = "100") int limit) {
        requireLogin();
        return R.ok(mallCsService.history(sessionNo, limit));
    }

    @Operation(summary = "客服排队列表")
    @PreAuthorize("hasAuthority('cs:desk')")
    @GetMapping("/admin/waiting")
    public R<List<MallCsSession>> waiting() {
        return R.ok(mallCsService.waitingQueue());
    }

    @Operation(summary = "我的接待中会话")
    @PreAuthorize("hasAuthority('cs:desk')")
    @GetMapping("/admin/mine")
    public R<List<MallCsSession>> mine() {
        LoginUser user = requireLogin();
        return R.ok(mallCsService.agentSessions(user.getId()));
    }

    @Operation(summary = "客服接入会话")
    @PreAuthorize("hasAuthority('cs:desk')")
    @PostMapping("/admin/assign")
    public R<MallCsSession> assign(@RequestBody Map<String, String> body) {
        LoginUser user = requireLogin();
        String name = user.getUserRealName() != null ? user.getUserRealName() : user.getUserName();
        return R.ok(mallCsService.assign(user.getId(), name, body.get("sessionNo")));
    }

    private LoginUser requireLogin() {
        LoginUser user = UserUtil.getUser();
        if (user == null) throw new ServiceException(401, "请先登录");
        return user;
    }
}
