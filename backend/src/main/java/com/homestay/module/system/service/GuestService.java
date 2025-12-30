package com.homestay.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homestay.module.system.entity.Guest;

public interface GuestService extends IService<Guest> {
    
    Guest login(String username, String password);
    
    Guest register(Guest guest);
    
    IPage<Guest> getGuestPage(Integer page, Integer size, String keyword);
    
    boolean updateGuest(Guest guest);
    
    boolean updatePassword(Long id, String oldPassword, String newPassword);
}
