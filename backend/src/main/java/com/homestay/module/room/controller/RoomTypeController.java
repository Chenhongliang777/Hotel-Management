package com.homestay.module.room.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homestay.common.result.PageResult;
import com.homestay.common.result.Result;
import com.homestay.module.room.entity.RoomType;
import com.homestay.module.room.service.RoomTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "房型管理")
@RestController
@RequestMapping("/roomType")
public class RoomTypeController {

    @Autowired
    private RoomTypeService roomTypeService;

    @Operation(summary = "获取房型分页列表")
    @GetMapping("/page")
    public Result<PageResult<RoomType>> getRoomTypePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        IPage<RoomType> pageResult = roomTypeService.getRoomTypePage(page, size, keyword, status);
        return Result.success(PageResult.of(pageResult));
    }

    @Operation(summary = "获取所有可用房型")
    @GetMapping("/available")
    public Result<List<RoomType>> getAvailableRoomTypes() {
        return Result.success(roomTypeService.getAvailableRoomTypes());
    }

    @Operation(summary = "搜索可用房型")
    @GetMapping("/search")
    public Result<List<RoomType>> searchAvailableRoomTypes(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate,
            @RequestParam(required = false) Integer guestCount) {
        return Result.success(roomTypeService.searchAvailableRoomTypes(checkInDate, checkOutDate, guestCount));
    }

    @Operation(summary = "获取房型详情")
    @GetMapping("/{id}")
    public Result<RoomType> getRoomType(@PathVariable Long id) {
        return Result.success(roomTypeService.getById(id));
    }

    @Operation(summary = "添加房型")
    @PostMapping
    public Result<Void> addRoomType(@RequestBody RoomType roomType) {
        roomTypeService.addRoomType(roomType);
        return Result.success();
    }

    @Operation(summary = "更新房型")
    @PutMapping
    public Result<Void> updateRoomType(@RequestBody RoomType roomType) {
        roomTypeService.updateRoomType(roomType);
        return Result.success();
    }

    @Operation(summary = "删除房型")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRoomType(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        return Result.success();
    }
}
