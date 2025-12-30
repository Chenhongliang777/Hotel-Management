package com.homestay.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.homestay.module.system.entity.Permission;

import java.util.List;

public interface PermissionService extends IService<Permission> {
    
    List<Permission> getPermissionTree();
    
    List<Permission> getPermissionsByRoleId(Long roleId);
}

