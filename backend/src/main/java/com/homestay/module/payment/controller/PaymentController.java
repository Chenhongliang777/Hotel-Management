package com.homestay.module.payment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homestay.common.result.PageResult;
import com.homestay.common.result.Result;
import com.homestay.module.payment.entity.PaymentRecord;
import com.homestay.module.payment.service.PaymentRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "支付管理")
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentRecordService paymentRecordService;

    @Operation(summary = "创建支付记录")
    @PostMapping
    public Result<PaymentRecord> createPayment(@RequestBody PaymentRecord payment) {
        return Result.success(paymentRecordService.createPayment(payment));
    }

    @Operation(summary = "获取支付记录分页列表")
    @GetMapping("/page")
    public Result<PageResult<PaymentRecord>> getPaymentPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String paymentType) {
        IPage<PaymentRecord> pageResult = paymentRecordService.getPaymentPage(page, size, orderNo, paymentType);
        return Result.success(PageResult.of(pageResult));
    }

    @Operation(summary = "获取订单支付记录")
    @GetMapping("/order/{orderId}")
    public Result<List<PaymentRecord>> getOrderPayments(@PathVariable Long orderId) {
        return Result.success(paymentRecordService.getOrderPayments(orderId));
    }

    @Operation(summary = "创建退款")
    @PostMapping("/refund")
    public Result<PaymentRecord> createRefund(@RequestBody RefundRequest request) {
        return Result.success(paymentRecordService.createRefund(
                request.getOrderId(), request.getAmount(), request.getRemark()));
    }

    @Data
    public static class RefundRequest {
        private Long orderId;
        private BigDecimal amount;
        private String remark;
    }
}
