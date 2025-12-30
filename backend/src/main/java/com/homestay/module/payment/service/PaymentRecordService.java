package com.homestay.module.payment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homestay.module.payment.entity.PaymentRecord;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRecordService extends IService<PaymentRecord> {
    
    PaymentRecord createPayment(PaymentRecord payment);
    
    IPage<PaymentRecord> getPaymentPage(Integer page, Integer size, String orderNo, String paymentType);
    
    List<PaymentRecord> getOrderPayments(Long orderId);
    
    BigDecimal getOrderPaidAmount(Long orderId);
    
    PaymentRecord createRefund(Long orderId, BigDecimal amount, String remark);
}
