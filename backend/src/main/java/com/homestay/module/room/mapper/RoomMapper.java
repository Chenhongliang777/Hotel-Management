package com.homestay.module.room.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.homestay.module.room.entity.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RoomMapper extends BaseMapper<Room> {
    
    @Select("SELECT r.*, rt.name as room_type_name, rt.base_price, rt.max_guests " +
            "FROM room r LEFT JOIN room_type rt ON r.room_type_id = rt.id " +
            "WHERE r.deleted = 0")
    IPage<Room> selectRoomWithType(Page<Room> page);

    @Select("SELECT r.id FROM room r " +
            "WHERE r.room_type_id = #{roomTypeId} " +
            "AND r.status = 'available' " +
            "AND r.deleted = 0 " +
            "AND r.id NOT IN (" +
            "  SELECT DISTINCT o.room_id FROM booking_order o " +
            "  WHERE o.deleted = 0 " +
            "  AND o.status IN ('confirmed', 'checked_in') " +
            "  AND o.room_id IS NOT NULL " +
            "  AND ((o.check_in_date <= #{checkInDate} AND o.check_out_date > #{checkInDate}) " +
            "       OR (o.check_in_date < #{checkOutDate} AND o.check_out_date >= #{checkOutDate}) " +
            "       OR (o.check_in_date >= #{checkInDate} AND o.check_out_date <= #{checkOutDate}))" +
            ") LIMIT 1")
    Long findAvailableRoom(@Param("roomTypeId") Long roomTypeId, 
                          @Param("checkInDate") LocalDate checkInDate, 
                          @Param("checkOutDate") LocalDate checkOutDate);
}
