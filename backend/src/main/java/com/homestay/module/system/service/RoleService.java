package com.homestay.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homestay.module.system.entity.Role;

import java.util.List;

public interface RoleService extends IService<Role> {
    
    IPage<Role> getRolePage(Integer page, Integer size, String keyword);
    
    boolean addRole(Role role);
    
    boolean updateRole(Role role);
    
    boolean deleteRole(Long id);
    
    List<Long> getRolePermissions(Long roleId);
    
    boolean assignPermissions(Long roleId, List<Long> permissionIds);
}

