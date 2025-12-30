package com.homestay.module.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homestay.common.exception.BusinessException;
import com.homestay.common.result.PageResult;
import com.homestay.common.result.Result;
import com.homestay.context.UserContextHolder;
import com.homestay.module.order.entity.Order;
import com.homestay.module.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "订单管理")
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Order> createOrder(@RequestBody Order order) {
        log.info("收到创建订单请求 - 房型ID: {}, 入住日期: {}, 退房日期: {}, 宾客姓名: {}, 宾客电话: {}, 入住人数: {}", 
                order.getRoomTypeId(), order.getCheckInDate(), order.getCheckOutDate(), 
                order.getGuestName(), order.getGuestPhone(), order.getGuestCount());
        try {
            Order createdOrder = orderService.createOrder(order);
            log.info("订单创建成功 - 订单ID: {}, 订单号: {}, 总价: {}, 保证金: {}", 
                    createdOrder.getId(), createdOrder.getOrderNo(), 
                    createdOrder.getTotalPrice(), createdOrder.getDeposit());
            return Result.success(createdOrder);
        } catch (Exception e) {
            log.error("订单创建失败 - 房型ID: {}, 入住日期: {}, 退房日期: {}, 错误信息: {}", 
                    order.getRoomTypeId(), order.getCheckInDate(), order.getCheckOutDate(), e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "获取订单分页列表")
    @GetMapping("/page")
    public Result<PageResult<Order>> getOrderPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        IPage<Order> pageResult = orderService.getOrderPage(page, size, keyword, status, startDate, endDate);
        return Result.success(PageResult.of(pageResult));
    }

    @Operation(summary = "获取当前宾客的订单")
    @GetMapping("/my")
    public Result<List<Order>> getMyOrders() {
        Long guestId = UserContextHolder.getUserId();
        return Result.success(orderService.getGuestOrders(guestId));
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<Order> getOrder(@PathVariable Long id) {
        try {
            log.info("获取订单详情 - 订单ID: {}", id);
            Order order = orderService.getOrderDetail(id);
            if (order == null) {
                log.warn("订单不存在 - 订单ID: {}", id);
                throw new BusinessException(404, "订单不存在");
            }
            log.debug("订单详情获取成功 - 订单ID: {}, 订单号: {}", id, order.getOrderNo());
            return Result.success(order);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取订单详情失败 - 订单ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "根据订单号获取订单")
    @GetMapping("/no/{orderNo}")
    public Result<Order> getOrderByNo(@PathVariable String orderNo) {
        return Result.success(orderService.getOrderByOrderNo(orderNo));
    }

    @Operation(summary = "确认订单")
    @PutMapping("/{id}/confirm")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return Result.success();
    }

    @Operation(summary = "办理入住")
    @PutMapping("/{id}/checkin")
    public Result<Void> checkIn(@PathVariable Long id, @RequestBody CheckInRequest request) {
        orderService.checkIn(id, request.getRoomId());
        return Result.success();
    }

    @Operation(summary = "办理退房")
    @PutMapping("/{id}/checkout")
    public Result<Void> checkOut(@PathVariable Long id) {
        orderService.checkOut(id);
        return Result.success();
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success();
    }

    @Operation(summary = "更新订单")
    @PutMapping
    public Result<Void> updateOrder(@RequestBody Order order) {
        orderService.updateOrder(order);
        return Result.success();
    }

    @Operation(summary = "今日待入住订单")
    @GetMapping("/today-checkin")
    public Result<List<Order>> getTodayCheckInOrders() {
        return Result.success(orderService.getTodayCheckInOrders());
    }

    @Operation(summary = "今日待退房订单")
    @GetMapping("/today-checkout")
    public Result<List<Order>> getTodayCheckOutOrders() {
        return Result.success(orderService.getTodayCheckOutOrders());
    }

    @Operation(summary = "获取订单统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getOrderStatistics() {
        return Result.success(orderService.getOrderStatistics());
    }

    @Data
    public static class CheckInRequest {
        private Long roomId;
    }
}
