package com.homestay.module.cleaning.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homestay.common.result.PageResult;
import com.homestay.common.result.Result;
import com.homestay.context.UserContextHolder;
import com.homestay.module.cleaning.entity.CleaningTask;
import com.homestay.module.cleaning.service.CleaningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "清洁任务管理")
@RestController
@RequestMapping("/cleaning")
public class CleaningController {

    @Autowired
    private CleaningService cleaningService;

    @Operation(summary = "创建清洁任务")
    @PostMapping
    public Result<CleaningTask> createTask(@RequestBody CleaningTask task) {
        return Result.success(cleaningService.createTask(task));
    }

    @Operation(summary = "获取任务分页列表")
    @GetMapping("/page")
    public Result<PageResult<CleaningTask>> getTaskPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long assigneeId) {
        IPage<CleaningTask> pageResult = cleaningService.getTaskPage(page, size, status, assigneeId);
        return Result.success(PageResult.of(pageResult));
    }

    @Operation(summary = "获取我的任务")
    @GetMapping("/my")
    public Result<List<CleaningTask>> getMyTasks() {
        Long userId = UserContextHolder.getUserId();
        return Result.success(cleaningService.getMyTasks(userId));
    }

    @Operation(summary = "获取待分配任务")
    @GetMapping("/pending")
    public Result<List<CleaningTask>> getPendingTasks() {
        return Result.success(cleaningService.getPendingTasks());
    }

    @Operation(summary = "分配任务")
    @PutMapping("/{id}/assign")
    public Result<Void> assignTask(@PathVariable Long id, @RequestBody AssignRequest request) {
        cleaningService.assignTask(id, request.getAssigneeId(), request.getAssigneeName());
        return Result.success();
    }

    @Operation(summary = "开始任务")
    @PutMapping("/{id}/start")
    public Result<Void> startTask(@PathVariable Long id) {
        cleaningService.startTask(id);
        return Result.success();
    }

    @Operation(summary = "完成任务")
    @PutMapping("/{id}/complete")
    public Result<Void> completeTask(@PathVariable Long id) {
        cleaningService.completeTask(id);
        return Result.success();
    }

    @Data
    public static class AssignRequest {
        private Long assigneeId;
        private String assigneeName;
    }
}
