package com.homestay.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homestay.common.exception.BusinessException;
import com.homestay.module.system.entity.Guest;
import com.homestay.module.system.mapper.GuestMapper;
import com.homestay.module.system.service.GuestService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GuestServiceImpl extends ServiceImpl<GuestMapper, Guest> implements GuestService {

    @Override
    public Guest login(String username, String password) {
        Guest guest = this.getOne(new LambdaQueryWrapper<Guest>()
                .eq(Guest::getUsername, username)
                .eq(Guest::getStatus, 1));
        
        if (guest == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (!password.equals(guest.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        return guest;
    }

    @Override
    public Guest register(Guest guest) {
        Guest existing = this.getOne(new LambdaQueryWrapper<Guest>()
                .eq(Guest::getUsername, guest.getUsername()));
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }
        
        if (StringUtils.hasText(guest.getPhone())) {
            Guest samePhone = this.getOne(new LambdaQueryWrapper<Guest>()
                    .eq(Guest::getPhone, guest.getPhone()));
            if (samePhone != null) {
                throw new BusinessException("手机号已被注册");
            }
        }
        
        guest.setStatus(1);
        this.save(guest);
        return guest;
    }

    @Override
    public IPage<Guest> getGuestPage(Integer page, Integer size, String keyword) {
        Page<Guest> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Guest> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Guest::getUsername, keyword)
                    .or().like(Guest::getNickname, keyword)
                    .or().like(Guest::getRealName, keyword)
                    .or().like(Guest::getPhone, keyword));
        }
        
        wrapper.orderByDesc(Guest::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public boolean updateGuest(Guest guest) {
        Guest existing = this.getById(guest.getId());
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        return this.updateById(guest);
    }

    @Override
    public boolean updatePassword(Long id, String oldPassword, String newPassword) {
        Guest guest = this.getById(id);
        if (guest == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!oldPassword.equals(guest.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        guest.setPassword(newPassword);
        return this.updateById(guest);
    }
}
