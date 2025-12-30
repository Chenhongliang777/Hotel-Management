package com.homestay.module.inventory.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homestay.common.result.PageResult;
import com.homestay.common.result.Result;
import com.homestay.module.inventory.entity.InventoryItem;
import com.homestay.module.inventory.entity.InventoryRecord;
import com.homestay.module.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "库存管理")
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Operation(summary = "获取物品分页列表")
    @GetMapping("/page")
    public Result<PageResult<InventoryItem>> getItemPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        IPage<InventoryItem> pageResult = inventoryService.getItemPage(page, size, keyword, category);
        return Result.success(PageResult.of(pageResult));
    }

    @Operation(summary = "获取所有物品")
    @GetMapping("/list")
    public Result<List<InventoryItem>> getAllItems() {
        return Result.success(inventoryService.getAllItems());
    }

    @Operation(summary = "获取低库存物品")
    @GetMapping("/low-stock")
    public Result<List<InventoryItem>> getLowStockItems() {
        return Result.success(inventoryService.getLowStockItems());
    }

    @Operation(summary = "获取物品详情")
    @GetMapping("/{id}")
    public Result<InventoryItem> getItem(@PathVariable Long id) {
        return Result.success(inventoryService.getById(id));
    }

    @Operation(summary = "添加物品")
    @PostMapping
    public Result<Void> addItem(@RequestBody InventoryItem item) {
        inventoryService.addItem(item);
        return Result.success();
    }

    @Operation(summary = "更新物品")
    @PutMapping
    public Result<Void> updateItem(@RequestBody InventoryItem item) {
        inventoryService.updateItem(item);
        return Result.success();
    }

    @Operation(summary = "删除物品")
    @DeleteMapping("/{id}")
    public Result<Void> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return Result.success();
    }

    @Operation(summary = "入库")
    @PostMapping("/stock-in")
    public Result<Void> stockIn(@RequestBody StockInRequest request) {
        inventoryService.stockIn(request.getItemId(), request.getQuantity(), request.getRemark());
        return Result.success();
    }

    @Operation(summary = "出库")
    @PostMapping("/stock-out")
    public Result<Void> stockOut(@RequestBody StockOutRequest request) {
        inventoryService.stockOut(request.getItemId(), request.getQuantity(), 
                request.getRoomId(), request.getOrderId(), request.getRemark());
        return Result.success();
    }

    @Operation(summary = "获取库存变动记录")
    @GetMapping("/record/page")
    public Result<PageResult<InventoryRecord>> getRecordPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) String type) {
        IPage<InventoryRecord> pageResult = inventoryService.getRecordPage(page, size, itemId, type);
        return Result.success(PageResult.of(pageResult));
    }

    @Data
    public static class StockInRequest {
        private Long itemId;
        private Integer quantity;
        private String remark;
    }

    @Data
    public static class StockOutRequest {
        private Long itemId;
        private Integer quantity;
        private Long roomId;
        private Long orderId;
        private String remark;
    }
}
