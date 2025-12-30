package com.homestay.module.pos.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homestay.module.pos.entity.PosRecord;

import java.math.BigDecimal;
import java.util.List;

public interface PosService extends IService<PosRecord> {
    
    PosRecord createPosRecord(PosRecord record);
    
    IPage<PosRecord> getPosPage(Integer page, Integer size, String orderNo, Long roomId);
    
    List<PosRecord> getOrderPosRecords(Long orderId);
    
    BigDecimal getOrderExtraCharges(Long orderId);
}
