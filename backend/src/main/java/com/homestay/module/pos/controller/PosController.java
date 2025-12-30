package com.homestay.module.pos.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homestay.common.result.PageResult;
import com.homestay.common.result.Result;
import com.homestay.module.pos.entity.PosRecord;
import com.homestay.module.pos.service.PosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "POS消费管理")
@RestController
@RequestMapping("/pos")
public class PosController {

    @Autowired
    private PosService posService;

    @Operation(summary = "创建消费记录")
    @PostMapping
    public Result<PosRecord> createPosRecord(@RequestBody PosRecord record) {
        return Result.success(posService.createPosRecord(record));
    }

    @Operation(summary = "获取消费记录分页列表")
    @GetMapping("/page")
    public Result<PageResult<PosRecord>> getPosPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long roomId) {
        IPage<PosRecord> pageResult = posService.getPosPage(page, size, orderNo, roomId);
        return Result.success(PageResult.of(pageResult));
    }

    @Operation(summary = "获取订单消费记录")
    @GetMapping("/order/{orderId}")
    public Result<List<PosRecord>> getOrderPosRecords(@PathVariable Long orderId) {
        return Result.success(posService.getOrderPosRecords(orderId));
    }
}
