package com.homestay.module.inventory.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homestay.module.inventory.entity.InventoryItem;
import com.homestay.module.inventory.entity.InventoryRecord;

import java.util.List;

public interface InventoryService extends IService<InventoryItem> {
    
    IPage<InventoryItem> getItemPage(Integer page, Integer size, String keyword, String category);
    
    List<InventoryItem> getAllItems();
    
    List<InventoryItem> getLowStockItems();
    
    boolean addItem(InventoryItem item);
    
    boolean updateItem(InventoryItem item);
    
    boolean deleteItem(Long id);
    
    boolean stockIn(Long itemId, Integer quantity, String remark);
    
    boolean stockOut(Long itemId, Integer quantity, Long roomId, Long orderId, String remark);
    
    IPage<InventoryRecord> getRecordPage(Integer page, Integer size, Long itemId, String type);
}
