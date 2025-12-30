package com.homestay.module.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homestay.common.exception.BusinessException;
import com.homestay.context.UserContextHolder;
import com.homestay.module.inventory.entity.InventoryItem;
import com.homestay.module.inventory.entity.InventoryRecord;
import com.homestay.module.inventory.mapper.InventoryItemMapper;
import com.homestay.module.inventory.mapper.InventoryRecordMapper;
import com.homestay.module.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryItemMapper, InventoryItem> implements InventoryService {

    @Autowired
    private InventoryRecordMapper recordMapper;

    @Override
    public IPage<InventoryItem> getItemPage(Integer page, Integer size, String keyword, String category) {
        Page<InventoryItem> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<InventoryItem> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(InventoryItem::getName, keyword);
        }
        
        if (StringUtils.hasText(category)) {
            wrapper.eq(InventoryItem::getCategory, category);
        }
        
        wrapper.orderByDesc(InventoryItem::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public List<InventoryItem> getAllItems() {
        return this.list(new LambdaQueryWrapper<InventoryItem>()
                .eq(InventoryItem::getStatus, 1)
                .orderByAsc(InventoryItem::getCategory)
                .orderByAsc(InventoryItem::getName));
    }

    @Override
    public List<InventoryItem> getLowStockItems() {
        return baseMapper.findLowStockItems();
    }

    @Override
    public boolean addItem(InventoryItem item) {
        item.setStatus(1);
        if (item.getQuantity() == null) {
            item.setQuantity(0);
        }
        return this.save(item);
    }

    @Override
    public boolean updateItem(InventoryItem item) {
        InventoryItem existing = this.getById(item.getId());
        if (existing == null) {
            throw new BusinessException("物品不存在");
        }
        return this.updateById(item);
    }

    @Override
    public boolean deleteItem(Long id) {
        InventoryItem item = this.getById(id);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        return this.removeById(id);
    }

    @Override
    @Transactional
    public boolean stockIn(Long itemId, Integer quantity, String remark) {
        InventoryItem item = this.getById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        
        InventoryRecord record = new InventoryRecord();
        record.setItemId(itemId);
        record.setType("in");
        record.setQuantity(quantity);
        record.setBeforeQuantity(item.getQuantity());
        record.setAfterQuantity(item.getQuantity() + quantity);
        record.setRemark(remark);
        record.setOperatorId(UserContextHolder.getUserId());
        recordMapper.insert(record);
        
        item.setQuantity(item.getQuantity() + quantity);
        return this.updateById(item);
    }

    @Override
    @Transactional
    public boolean stockOut(Long itemId, Integer quantity, Long roomId, Long orderId, String remark) {
        InventoryItem item = this.getById(itemId);
        if (item == null) {
            throw new BusinessException("物品不存在");
        }
        
        if (item.getQuantity() < quantity) {
            throw new BusinessException("库存不足");
        }
        
        InventoryRecord record = new InventoryRecord();
        record.setItemId(itemId);
        record.setType("out");
        record.setQuantity(quantity);
        record.setBeforeQuantity(item.getQuantity());
        record.setAfterQuantity(item.getQuantity() - quantity);
        record.setRoomId(roomId);
        record.setOrderId(orderId);
        record.setRemark(remark);
        record.setOperatorId(UserContextHolder.getUserId());
        recordMapper.insert(record);
        
        item.setQuantity(item.getQuantity() - quantity);
        return this.updateById(item);
    }

    @Override
    public IPage<InventoryRecord> getRecordPage(Integer page, Integer size, Long itemId, String type) {
        Page<InventoryRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<InventoryRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (itemId != null) {
            wrapper.eq(InventoryRecord::getItemId, itemId);
        }
        
        if (StringUtils.hasText(type)) {
            wrapper.eq(InventoryRecord::getType, type);
        }
        
        wrapper.orderByDesc(InventoryRecord::getCreateTime);
        return recordMapper.selectPage(pageParam, wrapper);
    }
}
