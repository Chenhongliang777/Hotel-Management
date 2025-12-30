package com.homestay.module.room.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homestay.common.result.PageResult;
import com.homestay.common.result.Result;
import com.homestay.module.room.entity.Room;
import com.homestay.module.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "房间管理")
@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Operation(summary = "获取房间分页列表")
    @GetMapping("/page")
    public Result<PageResult<Room>> getRoomPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long roomTypeId,
            @RequestParam(required = false) String status) {
        IPage<Room> pageResult = roomService.getRoomPage(page, size, keyword, roomTypeId, status);
        return Result.success(PageResult.of(pageResult));
    }

    @Operation(summary = "获取所有房间")
    @GetMapping("/list")
    public Result<List<Room>> getAllRooms() {
        return Result.success(roomService.getAllRooms());
    }

    @Operation(summary = "按房型获取房间")
    @GetMapping("/type/{roomTypeId}")
    public Result<List<Room>> getRoomsByType(@PathVariable Long roomTypeId) {
        return Result.success(roomService.getRoomsByType(roomTypeId));
    }

    @Operation(summary = "获取房间详情")
    @GetMapping("/{id}")
    public Result<Room> getRoom(@PathVariable Long id) {
        return Result.success(roomService.getRoomDetail(id));
    }

    @Operation(summary = "添加房间")
    @PostMapping
    public Result<Void> addRoom(@RequestBody Room room) {
        roomService.addRoom(room);
        return Result.success();
    }

    @Operation(summary = "更新房间")
    @PutMapping
    public Result<Void> updateRoom(@RequestBody Room room) {
        roomService.updateRoom(room);
        return Result.success();
    }

    @Operation(summary = "删除房间")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return Result.success();
    }

    @Operation(summary = "更新房间状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateRoomStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        roomService.updateRoomStatus(id, request.getStatus());
        return Result.success();
    }

    @Operation(summary = "更新房间清洁状态")
    @PutMapping("/{id}/clean-status")
    public Result<Void> updateCleanStatus(@PathVariable Long id, @RequestBody CleanStatusRequest request) {
        roomService.updateCleanStatus(id, request.getCleanStatus());
        return Result.success();
    }

    @Operation(summary = "获取房态统计")
    @GetMapping("/statistics")
    public Result<List<Map<String, Object>>> getRoomStatistics() {
        return Result.success(roomService.getRoomStatusStatistics());
    }

    @Data
    public static class StatusRequest {
        private String status;
    }

    @Data
    public static class CleanStatusRequest {
        private String cleanStatus;
    }
}
