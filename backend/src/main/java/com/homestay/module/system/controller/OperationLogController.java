package com.homestay.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homestay.common.result.PageResult;
import com.homestay.common.result.Result;
import com.homestay.module.system.entity.OperationLog;
import com.homestay.module.system.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "操作日志")
@RestController
@RequestMapping("/log")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @Operation(summary = "获取操作日志列表")
    @GetMapping("/page")
    public Result<PageResult<OperationLog>> getLogPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        IPage<OperationLog> pageResult = operationLogService.getLogPage(page, size, module, username, startDate, endDate);
        return Result.success(PageResult.of(pageResult));
    }
}
