package com.homestay.module.room.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homestay.module.room.entity.RoomType;

import java.time.LocalDate;
import java.util.List;

public interface RoomTypeService extends IService<RoomType> {
    
    IPage<RoomType> getRoomTypePage(Integer page, Integer size, String keyword, Integer status);
    
    List<RoomType> getAvailableRoomTypes();
    
    List<RoomType> searchAvailableRoomTypes(LocalDate checkInDate, LocalDate checkOutDate, Integer guestCount);
    
    boolean addRoomType(RoomType roomType);
    
    boolean updateRoomType(RoomType roomType);
    
    boolean deleteRoomType(Long id);
}
