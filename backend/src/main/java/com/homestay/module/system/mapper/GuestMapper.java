package com.homestay.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.homestay.module.system.entity.Guest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GuestMapper extends BaseMapper<Guest> {
}
