package com.homestay.module.statistics.controller;

import com.homestay.common.result.Result;
import com.homestay.module.inventory.service.InventoryService;
import com.homestay.module.order.service.OrderService;
import com.homestay.module.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "统计分析")
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private InventoryService inventoryService;

    @Operation(summary = "获取首页统计数据")
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.putAll(orderService.getOrderStatistics());
        stats.put("roomStats", roomService.getRoomStatusStatistics());
        stats.put("lowStockCount", inventoryService.getLowStockItems().size());
        
        return Result.success(stats);
    }

    @Operation(summary = "获取营收图表数据")
    @GetMapping("/revenue-chart")
    public Result<List<Map<String, Object>>> getRevenueChart(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return Result.success(orderService.getRevenueChart(startDate, endDate));
    }

    @Operation(summary = "获取房态统计")
    @GetMapping("/room-status")
    public Result<List<Map<String, Object>>> getRoomStatusStats() {
        return Result.success(roomService.getRoomStatusStatistics());
    }
}
