package com.homestay.module.room.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homestay.common.exception.BusinessException;
import com.homestay.module.room.entity.Room;
import com.homestay.module.room.entity.RoomType;
import com.homestay.module.room.mapper.RoomMapper;
import com.homestay.module.room.mapper.RoomTypeMapper;
import com.homestay.module.room.service.RoomService;
import com.homestay.module.cleaning.entity.CleaningTask;
import com.homestay.module.cleaning.service.CleaningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room> implements RoomService {

    @Autowired
    private RoomTypeMapper roomTypeMapper;

    @Autowired
    @Lazy
    private CleaningService cleaningService;

    @Override
    public IPage<Room> getRoomPage(Integer page, Integer size, String keyword, Long roomTypeId, String status) {
        Page<Room> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Room> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Room::getRoomNumber, keyword);
        }
        
        if (roomTypeId != null) {
            wrapper.eq(Room::getRoomTypeId, roomTypeId);
        }
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Room::getStatus, status);
        }
        
        wrapper.orderByAsc(Room::getRoomNumber);
        IPage<Room> result = this.page(pageParam, wrapper);
        
        for (Room room : result.getRecords()) {
            RoomType roomType = roomTypeMapper.selectById(room.getRoomTypeId());
            room.setRoomType(roomType);
        }
        
        return result;
    }

    @Override
    public List<Room> getAllRooms() {
        List<Room> rooms = this.list(new LambdaQueryWrapper<Room>()
                .orderByAsc(Room::getRoomNumber));
        for (Room room : rooms) {
            RoomType roomType = roomTypeMapper.selectById(room.getRoomTypeId());
            room.setRoomType(roomType);
        }
        return rooms;
    }

    @Override
    public List<Room> getRoomsByType(Long roomTypeId) {
        return this.list(new LambdaQueryWrapper<Room>()
                .eq(Room::getRoomTypeId, roomTypeId)
                .orderByAsc(Room::getRoomNumber));
    }

    @Override
    public Room getRoomDetail(Long id) {
        Room room = this.getById(id);
        if (room != null) {
            RoomType roomType = roomTypeMapper.selectById(room.getRoomTypeId());
            room.setRoomType(roomType);
        }
        return room;
    }

    @Override
    public boolean addRoom(Room room) {
        Room existing = this.getOne(new LambdaQueryWrapper<Room>()
                .eq(Room::getRoomNumber, room.getRoomNumber()));
        if (existing != null) {
            throw new BusinessException("房间号已存在");
        }
        
        // 如果未设置状态，默认为可用
        if (room.getStatus() == null || room.getStatus().isEmpty()) {
            room.setStatus("available");
        }
        if (room.getCleanStatus() == null || room.getCleanStatus().isEmpty()) {
            room.setCleanStatus("clean");
        }
        return this.save(room);
    }

    @Override
    public boolean updateRoom(Room room) {
        Room existing = this.getById(room.getId());
        if (existing == null) {
            throw new BusinessException("房间不存在");
        }
        
        Room sameNumber = this.getOne(new LambdaQueryWrapper<Room>()
                .eq(Room::getRoomNumber, room.getRoomNumber())
                .ne(Room::getId, room.getId()));
        if (sameNumber != null) {
            throw new BusinessException("房间号已存在");
        }
        
        return this.updateById(room);
    }

    @Override
    public boolean deleteRoom(Long id) {
        Room room = this.getById(id);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        if ("occupied".equals(room.getStatus())) {
            throw new BusinessException("房间正在使用中，无法删除");
        }
        return this.removeById(id);
    }

    @Override
    public boolean updateRoomStatus(Long id, String status) {
        Room room = this.getById(id);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        room.setStatus(status);
        return this.updateById(room);
    }

    @Override
    public boolean updateCleanStatus(Long id, String cleanStatus) {
        Room room = this.getById(id);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        
        String oldStatus = room.getCleanStatus();
        room.setCleanStatus(cleanStatus);
        boolean result = this.updateById(room);
        
        // 当房间被标记为待清洁时，自动创建清洁任务
        if (result && "dirty".equals(cleanStatus) && !"dirty".equals(oldStatus)) {
            CleaningTask task = new CleaningTask();
            task.setRoomId(room.getId());
            task.setRoomNumber(room.getRoomNumber());
            task.setTaskType("日常清洁");
            task.setStatus("pending");
            cleaningService.createTask(task);
        }
        
        // 当房间被标记为已清洁时，取消该房间所有待分配的清洁任务
        if (result && "clean".equals(cleanStatus) && !"clean".equals(oldStatus)) {
            cleaningService.cancelPendingTasksByRoomId(id);
        }
        
        return result;
    }

    @Override
    public Long findAvailableRoom(Long roomTypeId, LocalDate checkInDate, LocalDate checkOutDate) {
        return baseMapper.findAvailableRoom(roomTypeId, checkInDate, checkOutDate);
    }

    @Override
    public List<Map<String, Object>> getRoomStatusStatistics() {
        List<Map<String, Object>> stats = new ArrayList<>();
        
        String[] statuses = {"available", "occupied", "reserved", "maintenance"};
        String[] labels = {"可用", "已入住", "已预订", "维修中"};
        
        for (int i = 0; i < statuses.length; i++) {
            Map<String, Object> item = new HashMap<>();
            long count = this.count(new LambdaQueryWrapper<Room>()
                    .eq(Room::getStatus, statuses[i]));
            item.put("status", statuses[i]);
            item.put("label", labels[i]);
            item.put("count", count);
            stats.add(item);
        }
        
        return stats;
    }
}
