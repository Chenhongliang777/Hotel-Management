package com.homestay.module.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homestay.module.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    @Select("SELECT COALESCE(SUM(total_price), 0) FROM booking_order " +
            "WHERE DATE(create_time) = #{date} AND status NOT IN ('cancelled') AND deleted = 0")
    BigDecimal getDailyRevenue(@Param("date") LocalDate date);

    @Select("SELECT COALESCE(SUM(total_price), 0) FROM booking_order " +
            "WHERE YEAR(create_time) = #{year} AND MONTH(create_time) = #{month} " +
            "AND status NOT IN ('cancelled') AND deleted = 0")
    BigDecimal getMonthlyRevenue(@Param("year") int year, @Param("month") int month);

    @Select("SELECT COUNT(*) FROM booking_order " +
            "WHERE DATE(create_time) = #{date} AND deleted = 0")
    Integer getDailyOrderCount(@Param("date") LocalDate date);

    @Select("SELECT DATE(create_time) as date, COUNT(*) as count, COALESCE(SUM(total_price), 0) as revenue " +
            "FROM booking_order " +
            "WHERE create_time >= #{startDate} AND create_time < #{endDate} " +
            "AND status NOT IN ('cancelled') AND deleted = 0 " +
            "GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> getOrderStatsByDateRange(@Param("startDate") LocalDate startDate, 
                                                        @Param("endDate") LocalDate endDate);
}
