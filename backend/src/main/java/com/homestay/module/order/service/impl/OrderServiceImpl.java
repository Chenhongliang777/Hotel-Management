package com.homestay.module.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homestay.common.exception.BusinessException;
import com.homestay.context.UserContextHolder;
import com.homestay.module.order.entity.Order;
import com.homestay.module.order.mapper.OrderMapper;
import com.homestay.module.order.service.OrderService;
import com.homestay.module.room.entity.Room;
import com.homestay.module.room.entity.RoomType;
import com.homestay.module.room.mapper.RoomMapper;
import com.homestay.module.room.mapper.RoomTypeMapper;
import com.homestay.module.cleaning.entity.CleaningTask;
import com.homestay.module.cleaning.service.CleaningService;
import com.homestay.module.system.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private RoomMapper roomMapper;
    
    @Autowired
    private RoomTypeMapper roomTypeMapper;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired
    private CleaningService cleaningService;

    @Override
    @Transactional
    public Order createOrder(Order order) {
        log.info("开始创建订单 - 房型ID: {}, 入住日期: {}, 退房日期: {}, 宾客: {} ({})", 
                order.getRoomTypeId(), order.getCheckInDate(), order.getCheckOutDate(), 
                order.getGuestName(), order.getGuestPhone());
        
        // 验证房型
        RoomType roomType = roomTypeMapper.selectById(order.getRoomTypeId());
        if (roomType == null) {
            log.error("订单创建失败 - 房型不存在, 房型ID: {}", order.getRoomTypeId());
            throw new BusinessException("房型不存在");
        }
        log.debug("房型验证通过 - 房型名称: {}, 基础价格: {}", roomType.getName(), roomType.getBasePrice());
        
        // 查找可用房间
        Long availableRoomId = roomMapper.findAvailableRoom(order.getRoomTypeId(), 
                order.getCheckInDate(), order.getCheckOutDate());
        if (availableRoomId == null) {
            log.error("订单创建失败 - 该房型在所选日期内没有可用房间, 房型ID: {}, 入住日期: {}, 退房日期: {}", 
                    order.getRoomTypeId(), order.getCheckInDate(), order.getCheckOutDate());
            throw new BusinessException("该房型在所选日期内没有可用房间");
        }
        log.debug("找到可用房间 - 房间ID: {}", availableRoomId);
        
        // 计算入住晚数
        long nights = ChronoUnit.DAYS.between(order.getCheckInDate(), order.getCheckOutDate());
        if (nights <= 0) {
            log.error("订单创建失败 - 退房日期必须晚于入住日期, 入住日期: {}, 退房日期: {}", 
                    order.getCheckInDate(), order.getCheckOutDate());
            throw new BusinessException("退房日期必须晚于入住日期");
        }
        log.debug("计算入住晚数: {} 晚", nights);
        
        // 生成订单号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setNights((int) nights);
        order.setRoomPrice(roomType.getBasePrice());
        order.setTotalPrice(roomType.getBasePrice().multiply(BigDecimal.valueOf(nights)));
        log.debug("订单价格计算 - 单价: {}, 晚数: {}, 总价: {}", 
                roomType.getBasePrice(), nights, order.getTotalPrice());
        
        // 计算保证金
        BigDecimal depositRate = systemConfigService.getConfigDecimal("deposit_rate");
        order.setDeposit(order.getTotalPrice().multiply(depositRate));
        log.debug("保证金计算 - 保证金率: {}, 保证金: {}", depositRate, order.getDeposit());
        
        // 设置初始值
        // 从用户上下文获取当前登录用户的ID并设置到订单中
        Long guestId = UserContextHolder.getUserId();
        if (guestId == null) {
            log.error("创建订单失败 - 未获取到当前登录用户ID，请确保已登录");
            throw new BusinessException("请先登录后再创建订单");
        }
        order.setGuestId(guestId);
        log.debug("设置订单宾客ID - 宾客ID: {}", guestId);
        order.setPaidAmount(BigDecimal.ZERO);
        order.setExtraCharges(BigDecimal.ZERO);
        order.setStatus("pending");
        
        // 保存订单
        try {
            boolean saved = this.save(order);
            if (!saved) {
                log.error("订单保存失败 - 订单号: {}, 房型ID: {}", orderNo, order.getRoomTypeId());
                throw new BusinessException("订单保存失败，请稍后重试");
            }
            log.info("订单保存成功 - 订单ID: {}, 订单号: {}, 总价: {}, 保证金: {}", 
                    order.getId(), orderNo, order.getTotalPrice(), order.getDeposit());
        } catch (Exception e) {
            log.error("订单保存异常 - 订单号: {}, 错误信息: {}", orderNo, e.getMessage(), e);
            throw new BusinessException("订单保存失败: " + e.getMessage());
        }
        
        return order;
    }

    private String generateOrderNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = this.count(new LambdaQueryWrapper<Order>()
                .likeRight(Order::getOrderNo, "ORD" + date));
        return String.format("ORD%s%04d", date, count + 1);
    }

    @Override
    public IPage<Order> getOrderPage(Integer page, Integer size, String keyword, String status, String startDate, String endDate) {
        Page<Order> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Order::getOrderNo, keyword)
                    .or().like(Order::getGuestName, keyword)
                    .or().like(Order::getGuestPhone, keyword));
        }
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        
        if (StringUtils.hasText(startDate)) {
            wrapper.ge(Order::getCheckInDate, LocalDate.parse(startDate));
        }
        
        if (StringUtils.hasText(endDate)) {
            wrapper.le(Order::getCheckInDate, LocalDate.parse(endDate));
        }
        
        wrapper.orderByDesc(Order::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public List<Order> getGuestOrders(Long guestId) {
        List<Order> orders = this.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getGuestId, guestId)
                .orderByDesc(Order::getCreateTime));
        // 填充房型名称
        for (Order order : orders) {
            if (order.getRoomTypeId() != null) {
                RoomType roomType = roomTypeMapper.selectById(order.getRoomTypeId());
                if (roomType != null) {
                    order.setRoomTypeName(roomType.getName());
                }
            }
        }
        return orders;
    }

    @Override
    public Order getOrderDetail(Long id) {
        log.debug("查询订单详情 - 订单ID: {}", id);
        if (id == null) {
            log.error("订单ID不能为空");
            throw new BusinessException("订单ID不能为空");
        }
        Order order = this.getById(id);
        if (order == null) {
            log.warn("订单不存在 - 订单ID: {}", id);
            throw new BusinessException(404, "订单不存在");
        }
        // 填充房型名称
        if (order.getRoomTypeId() != null) {
            RoomType roomType = roomTypeMapper.selectById(order.getRoomTypeId());
            if (roomType != null) {
                order.setRoomTypeName(roomType.getName());
            }
        }
        log.debug("订单查询成功 - 订单ID: {}, 订单号: {}, 状态: {}", id, order.getOrderNo(), order.getStatus());
        return order;
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return this.getOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));
    }

    @Override
    @Transactional
    public boolean confirmOrder(Long id) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许确认");
        }
        order.setStatus("confirmed");
        return this.updateById(order);
    }

    @Override
    @Transactional
    public boolean checkIn(Long id, Long roomId) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"confirmed".equals(order.getStatus()) && !"pending".equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许办理入住");
        }
        
        Room room = roomMapper.selectById(roomId);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        if (!"available".equals(room.getStatus())) {
            throw new BusinessException("房间不可用");
        }
        
        order.setRoomId(roomId);
        order.setStatus("checked_in");
        order.setCheckInTime(LocalDateTime.now());
        order.setOperatorId(UserContextHolder.getUserId());
        this.updateById(order);
        
        room.setStatus("occupied");
        roomMapper.updateById(room);
        
        return true;
    }

    @Override
    @Transactional
    public boolean checkOut(Long id) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"checked_in".equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许退房");
        }
        
        order.setStatus("checked_out");
        order.setCheckOutTime(LocalDateTime.now());
        order.setOperatorId(UserContextHolder.getUserId());
        this.updateById(order);
        
        if (order.getRoomId() != null) {
            Room room = roomMapper.selectById(order.getRoomId());
            if (room != null) {
                room.setStatus("available");
                room.setCleanStatus("dirty");
                roomMapper.updateById(room);
                
                // 创建退房清洁任务
                CleaningTask task = new CleaningTask();
                task.setRoomId(room.getId());
                task.setRoomNumber(room.getRoomNumber());
                task.setTaskType("退房清洁");
                task.setStatus("pending");
                cleaningService.createTask(task);
            }
        }
        
        return true;
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long id) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if ("checked_in".equals(order.getStatus()) || "checked_out".equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许取消");
        }
        
        order.setStatus("cancelled");
        return this.updateById(order);
    }

    @Override
    public boolean updateOrder(Order order) {
        Order existing = this.getById(order.getId());
        if (existing == null) {
            throw new BusinessException("订单不存在");
        }
        // 检查是否已经改订过
        Integer modifyCount = existing.getModifyCount();
        if (modifyCount != null && modifyCount >= 1) {
            throw new BusinessException("订单已改订过，每个订单只能改订一次");
        }
        // 增加改订次数
        order.setModifyCount((modifyCount == null ? 0 : modifyCount) + 1);
        return this.updateById(order);
    }

    @Override
    public List<Order> getTodayCheckInOrders() {
        LocalDate today = LocalDate.now();
        // 只显示已确认的订单，待确认的订单需要先确认后才能办理入住
        List<Order> orders = this.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getCheckInDate, today)
                .eq(Order::getStatus, "confirmed")
                .orderByAsc(Order::getCreateTime));
        // 填充房型名称
        for (Order order : orders) {
            if (order.getRoomTypeId() != null) {
                RoomType roomType = roomTypeMapper.selectById(order.getRoomTypeId());
                if (roomType != null) {
                    order.setRoomTypeName(roomType.getName());
                }
            }
        }
        return orders;
    }

    @Override
    public List<Order> getTodayCheckOutOrders() {
        // 返回所有已入住的订单，可以随时办理退房
        List<Order> orders = this.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, "checked_in")
                .orderByAsc(Order::getCheckOutDate));
        // 填充房型名称
        for (Order order : orders) {
            if (order.getRoomTypeId() != null) {
                RoomType roomType = roomTypeMapper.selectById(order.getRoomTypeId());
                if (roomType != null) {
                    order.setRoomTypeName(roomType.getName());
                }
            }
        }
        return orders;
    }

    @Override
    public BigDecimal getDailyRevenue(LocalDate date) {
        return baseMapper.getDailyRevenue(date);
    }

    @Override
    public BigDecimal getMonthlyRevenue(int year, int month) {
        return baseMapper.getMonthlyRevenue(year, month);
    }

    @Override
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        
        stats.put("todayOrders", this.count(new LambdaQueryWrapper<Order>()
                .apply("DATE(create_time) = {0}", today)));
        
        stats.put("todayCheckIn", this.count(new LambdaQueryWrapper<Order>()
                .eq(Order::getCheckInDate, today)
                .in(Order::getStatus, "confirmed", "pending")));
        
        stats.put("todayCheckOut", this.count(new LambdaQueryWrapper<Order>()
                .eq(Order::getCheckOutDate, today)
                .eq(Order::getStatus, "checked_in")));
        
        stats.put("currentGuests", this.count(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, "checked_in")));
        
        stats.put("pendingOrders", this.count(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, "pending")));
        
        stats.put("todayRevenue", getDailyRevenue(today));
        stats.put("monthlyRevenue", getMonthlyRevenue(today.getYear(), today.getMonthValue()));
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getRevenueChart(LocalDate startDate, LocalDate endDate) {
        return baseMapper.getOrderStatsByDateRange(startDate, endDate.plusDays(1));
    }
}
