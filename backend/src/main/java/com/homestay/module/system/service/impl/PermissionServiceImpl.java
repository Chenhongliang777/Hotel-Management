package com.homestay.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homestay.module.system.entity.Permission;
import com.homestay.module.system.mapper.PermissionMapper;
import com.homestay.module.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Permission> getPermissionTree() {
        List<Permission> allPermissions = this.list(new LambdaQueryWrapper<Permission>()
                .eq(Permission::getStatus, 1)
                .orderByAsc(Permission::getSortOrder));
        
        // 构建树形结构
        Map<Long, List<Permission>> childrenMap = allPermissions.stream()
                .filter(p -> p.getParentId() != null && p.getParentId() > 0)
                .collect(Collectors.groupingBy(Permission::getParentId));
        
        List<Permission> rootPermissions = allPermissions.stream()
                .filter(p -> p.getParentId() == null || p.getParentId() == 0)
                .collect(Collectors.toList());
        
        for (Permission root : rootPermissions) {
            buildTree(root, childrenMap);
        }
        
        return rootPermissions;
    }

    private void buildTree(Permission parent, Map<Long, List<Permission>> childrenMap) {
        List<Permission> children = childrenMap.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            parent.setChildren(children);
            for (Permission child : children) {
                buildTree(child, childrenMap);
            }
        } else {
            parent.setChildren(new ArrayList<>());
        }
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        String sql = "SELECT p.* FROM sys_permission p " +
                     "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
                     "WHERE rp.role_id = ? AND p.status = 1 " +
                     "ORDER BY p.sort_order";
        
        return jdbcTemplate.query(sql, 
                (rs, rowNum) -> {
                    Permission p = new Permission();
                    p.setId(rs.getLong("id"));
                    p.setPermissionCode(rs.getString("permission_code"));
                    p.setPermissionName(rs.getString("permission_name"));
                    p.setModule(rs.getString("module"));
                    p.setDescription(rs.getString("description"));
                    p.setParentId(rs.getLong("parent_id"));
                    p.setSortOrder(rs.getInt("sort_order"));
                    p.setStatus(rs.getInt("status"));
                    return p;
                }, 
                roleId);
    }
}

