package com.homestay.module.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homestay.module.inventory.entity.InventoryItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InventoryItemMapper extends BaseMapper<InventoryItem> {
    
    @Select("SELECT * FROM inventory_item WHERE quantity <= min_stock AND status = 1 AND deleted = 0")
    List<InventoryItem> findLowStockItems();
}
