package com.homestay.module.pos.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homestay.module.pos.entity.PosRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface PosRecordMapper extends BaseMapper<PosRecord> {
    
    @Select("SELECT COALESCE(SUM(total_price), 0) FROM pos_record WHERE order_id = #{orderId}")
    BigDecimal getTotalByOrderId(@Param("orderId") Long orderId);
}
