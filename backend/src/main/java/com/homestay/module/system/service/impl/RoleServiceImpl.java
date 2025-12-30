package com.homestay.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homestay.common.exception.BusinessException;
import com.homestay.module.system.entity.Role;
import com.homestay.module.system.mapper.RoleMapper;
import com.homestay.module.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public IPage<Role> getRolePage(Integer page, Integer size, String keyword) {
        Page<Role> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Role::getRoleName, keyword)
                    .or().like(Role::getRoleCode, keyword)
                    .or().like(Role::getDescription, keyword));
        }
        
        wrapper.orderByDesc(Role::getRoleLevel);
        wrapper.orderByDesc(Role::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public boolean addRole(Role role) {
        Role existing = this.getOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, role.getRoleCode()));
        if (existing != null) {
            throw new BusinessException("角色编码已存在");
        }
        
        role.setStatus(1);
        return this.save(role);
    }

    @Override
    public boolean updateRole(Role role) {
        Role existing = this.getById(role.getId());
        if (existing == null) {
            throw new BusinessException("角色不存在");
        }
        
        Role sameCode = this.getOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, role.getRoleCode())
                .ne(Role::getId, role.getId()));
        if (sameCode != null) {
            throw new BusinessException("角色编码已存在");
        }
        
        return this.updateById(role);
    }

    @Override
    public boolean deleteRole(Long id) {
        Role role = this.getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 检查是否有员工使用此角色
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_employee_role WHERE role_id = ?", 
                Long.class, id);
        if (count != null && count > 0) {
            throw new BusinessException("该角色已被员工使用，无法删除");
        }
        
        return this.removeById(id);
    }

    @Override
    public List<Long> getRolePermissions(Long roleId) {
        String sql = "SELECT permission_id FROM sys_role_permission WHERE role_id = ?";
        List<Long> permissionIds = jdbcTemplate.queryForList(sql, Long.class, roleId);
        return permissionIds;
    }

    @Override
    @Transactional
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // 删除原有权限
        jdbcTemplate.update("DELETE FROM sys_role_permission WHERE role_id = ?", roleId);
        
        // 添加新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            String sql = "INSERT INTO sys_role_permission (role_id, permission_id) VALUES (?, ?)";
            List<Object[]> batchArgs = permissionIds.stream()
                    .map(pid -> new Object[]{roleId, pid})
                    .collect(Collectors.toList());
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }
        
        return true;
    }
}

