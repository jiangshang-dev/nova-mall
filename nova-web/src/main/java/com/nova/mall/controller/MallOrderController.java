package com.nova.mall.controller;

import com.nova.mall.base.PageResponse;
import com.nova.mall.debounce.annotation.Debounce;
import com.nova.mall.dto.OrderCreateDTO;
import com.nova.mall.log.annotation.OperLog;
import com.nova.mall.log.enums.BusinessType;
import com.nova.mall.service.MallOrderService;
import com.nova.mall.utils.R;
import com.nova.mall.vo.OrderVO;
import com.nova.mall.vo.PayInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "订单")
@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class MallOrderController {
    private final MallOrderService mallOrderService;

    @Operation(summary = "创建订单")
    @Debounce
    @OperLog(title = "订单", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public R<OrderVO> create(@Valid @RequestBody OrderCreateDTO dto) {
        return R.ok(mallOrderService.create(dto));
    }

    @Operation(summary = "我的订单分页")
    @GetMapping("/mine")
    public R<PageResponse<OrderVO>> mine(@RequestParam(defaultValue = "1") long pageNum,
                                         @RequestParam(defaultValue = "10") long pageSize) {
        return R.ok(PageResponse.res(mallOrderService.pageMine(pageNum, pageSize)));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{orderNo}")
    public R<OrderVO> detail(@PathVariable String orderNo) {
        return R.ok(mallOrderService.detailMine(orderNo));
    }

    @Operation(summary = "发起支付（支付宝/微信）")
    @PostMapping("/{orderNo}/pay")
    public R<PayInfoVO> pay(@PathVariable String orderNo, @RequestBody(required = false) Map<String, String> body) {
        String payType = body == null ? null : body.get("payType");
        return R.ok(mallOrderService.preparePay(orderNo, payType));
    }

    @Operation(summary = "模拟支付成功（演示环境）")
    @Debounce
    @PostMapping("/{orderNo}/pay/mock-success")
    public R<OrderVO> mockPay(@PathVariable String orderNo) {
        return R.ok(mallOrderService.mockPaySuccess(orderNo));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{orderNo}/cancel")
    public R<Void> cancel(@PathVariable String orderNo) {
        mallOrderService.cancelMine(orderNo);
        return R.ok();
    }

    @Operation(summary = "后台订单分页")
    @PreAuthorize("hasAuthority('order:list')")
    @GetMapping("/admin/page")
    public R<PageResponse<OrderVO>> adminPage(@RequestParam(defaultValue = "1") long pageNum,
                                              @RequestParam(defaultValue = "10") long pageSize,
                                              @RequestParam(required = false) String orderNo,
                                              @RequestParam(required = false) Integer status) {
        return R.ok(PageResponse.res(mallOrderService.pageAdmin(pageNum, pageSize, orderNo, status)));
    }

    @Operation(summary = "后台发货")
    @Debounce
    @OperLog(title = "订单发货", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('order:list')")
    @PostMapping("/admin/{orderNo}/ship")
    public R<OrderVO> ship(@PathVariable String orderNo) {
        return R.ok(mallOrderService.ship(orderNo));
    }

    @Operation(summary = "货到付款核销完成")
    @Debounce
    @OperLog(title = "订单核销", businessType = BusinessType.UPDATE)
    @PreAuthorize("hasAuthority('order:list')")
    @PostMapping("/admin/{orderNo}/verify-cod")
    public R<OrderVO> verifyCod(@PathVariable String orderNo) {
        return R.ok(mallOrderService.verifyCod(orderNo));
    }
}
