package com.homestay.module.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homestay.module.order.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order> {
    
    Order createOrder(Order order);
    
    IPage<Order> getOrderPage(Integer page, Integer size, String keyword, String status, String startDate, String endDate);
    
    List<Order> getGuestOrders(Long guestId);
    
    Order getOrderDetail(Long id);
    
    Order getOrderByOrderNo(String orderNo);
    
    boolean confirmOrder(Long id);
    
    boolean checkIn(Long id, Long roomId);
    
    boolean checkOut(Long id);
    
    boolean cancelOrder(Long id);
    
    boolean updateOrder(Order order);
    
    List<Order> getTodayCheckInOrders();
    
    List<Order> getTodayCheckOutOrders();
    
    BigDecimal getDailyRevenue(LocalDate date);
    
    BigDecimal getMonthlyRevenue(int year, int month);
    
    Map<String, Object> getOrderStatistics();
    
    List<Map<String, Object>> getRevenueChart(LocalDate startDate, LocalDate endDate);
}
