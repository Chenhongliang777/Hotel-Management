package com.homestay.module.room.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homestay.module.room.entity.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RoomService extends IService<Room> {
    
    IPage<Room> getRoomPage(Integer page, Integer size, String keyword, Long roomTypeId, String status);
    
    List<Room> getAllRooms();
    
    List<Room> getRoomsByType(Long roomTypeId);
    
    Room getRoomDetail(Long id);
    
    boolean addRoom(Room room);
    
    boolean updateRoom(Room room);
    
    boolean deleteRoom(Long id);
    
    boolean updateRoomStatus(Long id, String status);
    
    boolean updateCleanStatus(Long id, String cleanStatus);
    
    Long findAvailableRoom(Long roomTypeId, LocalDate checkInDate, LocalDate checkOutDate);
    
    List<Map<String, Object>> getRoomStatusStatistics();
}
