package com.homestay.module.pos.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homestay.common.exception.BusinessException;
import com.homestay.context.UserContextHolder;
import com.homestay.module.inventory.entity.InventoryItem;
import com.homestay.module.inventory.service.InventoryService;
import com.homestay.module.order.entity.Order;
import com.homestay.module.order.mapper.OrderMapper;
import com.homestay.module.pos.entity.PosRecord;
import com.homestay.module.pos.mapper.PosRecordMapper;
import com.homestay.module.pos.service.PosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PosServiceImpl extends ServiceImpl<PosRecordMapper, PosRecord> implements PosService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private InventoryService inventoryService;

    @Override
    @Transactional
    public PosRecord createPosRecord(PosRecord record) {
        Order order = orderMapper.selectById(record.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        record.setOrderNo(order.getOrderNo());
        record.setGuestId(order.getGuestId());
        record.setRoomId(order.getRoomId());
        record.setTotalPrice(record.getUnitPrice().multiply(BigDecimal.valueOf(record.getQuantity())));
        record.setOperatorId(UserContextHolder.getUserId());
        
        this.save(record);
        
        if (record.getItemId() != null) {
            inventoryService.stockOut(record.getItemId(), record.getQuantity(), 
                    order.getRoomId(), order.getId(), "POS消费扣减");
        }
        
        order.setExtraCharges(order.getExtraCharges().add(record.getTotalPrice()));
        orderMapper.updateById(order);
        
        return record;
    }

    @Override
    public IPage<PosRecord> getPosPage(Integer page, Integer size, String orderNo, Long roomId) {
        Page<PosRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PosRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(orderNo)) {
            wrapper.like(PosRecord::getOrderNo, orderNo);
        }
        
        if (roomId != null) {
            wrapper.eq(PosRecord::getRoomId, roomId);
        }
        
        wrapper.orderByDesc(PosRecord::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public List<PosRecord> getOrderPosRecords(Long orderId) {
        return this.list(new LambdaQueryWrapper<PosRecord>()
                .eq(PosRecord::getOrderId, orderId)
                .orderByDesc(PosRecord::getCreateTime));
    }

    @Override
    public BigDecimal getOrderExtraCharges(Long orderId) {
        return baseMapper.getTotalByOrderId(orderId);
    }
}
