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
import com.homestay.module.room.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoomTypeServiceImpl extends ServiceImpl<RoomTypeMapper, RoomType> implements RoomTypeService {

    @Autowired
    private RoomMapper roomMapper;

    @Override
    public IPage<RoomType> getRoomTypePage(Integer page, Integer size, String keyword, Integer status) {
        Page<RoomType> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<RoomType> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(RoomType::getName, keyword);
        }
        
        if (status != null) {
            wrapper.eq(RoomType::getStatus, status);
        }
        
        wrapper.orderByAsc(RoomType::getSortOrder);
        return this.page(pageParam, wrapper);
    }

    @Override
    public List<RoomType> getAvailableRoomTypes() {
        return this.list(new LambdaQueryWrapper<RoomType>()
                .eq(RoomType::getStatus, 1)
                .orderByAsc(RoomType::getSortOrder));
    }

    @Override
    public List<RoomType> searchAvailableRoomTypes(LocalDate checkInDate, LocalDate checkOutDate, Integer guestCount) {
        LambdaQueryWrapper<RoomType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomType::getStatus, 1);
        
        if (guestCount != null && guestCount > 0) {
            wrapper.ge(RoomType::getMaxGuests, guestCount);
        }
        
        wrapper.orderByAsc(RoomType::getSortOrder);
        List<RoomType> roomTypes = this.list(wrapper);
        
        // 如果日期为空，直接返回所有符合条件的房型（不检查可用性）
        if (checkInDate == null || checkOutDate == null) {
            return roomTypes;
        }
        
        // 如果日期不为空，检查每个房型在指定日期范围内是否有可用房间
        List<RoomType> availableTypes = new ArrayList<>();
        for (RoomType roomType : roomTypes) {
            Long availableRoomId = roomMapper.findAvailableRoom(roomType.getId(), checkInDate, checkOutDate);
            if (availableRoomId != null) {
                availableTypes.add(roomType);
            }
        }
        
        return availableTypes;
    }

    @Override
    public boolean addRoomType(RoomType roomType) {
        roomType.setStatus(1);
        return this.save(roomType);
    }

    @Override
    public boolean updateRoomType(RoomType roomType) {
        RoomType existing = this.getById(roomType.getId());
        if (existing == null) {
            throw new BusinessException("房型不存在");
        }
        return this.updateById(roomType);
    }

    @Override
    public boolean deleteRoomType(Long id) {
        long roomCount = roomMapper.selectCount(new LambdaQueryWrapper<Room>()
                .eq(Room::getRoomTypeId, id));
        if (roomCount > 0) {
            throw new BusinessException("该房型下还有房间，无法删除");
        }
        return this.removeById(id);
    }
}
