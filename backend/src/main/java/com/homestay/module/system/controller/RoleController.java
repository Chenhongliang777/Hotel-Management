package com.homestay.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homestay.common.result.PageResult;
import com.homestay.common.result.Result;
import com.homestay.module.system.entity.Role;
import com.homestay.module.system.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "获取角色列表")
    @GetMapping("/page")
    public Result<PageResult<Role>> getRolePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        IPage<Role> pageResult = roleService.getRolePage(page, size, keyword);
        return Result.success(PageResult.of(pageResult));
    }

    @Operation(summary = "获取所有角色")
    @GetMapping("/list")
    public Result<List<Role>> getAllRoles() {
        List<Role> roles = roleService.list();
        return Result.success(roles);
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    public Result<Role> getRole(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }

    @Operation(summary = "添加角色")
    @PostMapping
    public Result<Void> addRole(@RequestBody Role role) {
        roleService.addRole(role);
        return Result.success();
    }

    @Operation(summary = "更新角色")
    @PutMapping
    public Result<Void> updateRole(@RequestBody Role role) {
        roleService.updateRole(role);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    @Operation(summary = "获取角色权限")
    @GetMapping("/{id}/permissions")
    public Result<List<Long>> getRolePermissions(@PathVariable Long id) {
        List<Long> permissionIds = roleService.getRolePermissions(id);
        return Result.success(permissionIds);
    }

    @Operation(summary = "分配角色权限")
    @PostMapping("/{id}/permissions")
    public Result<Void> assignPermissions(@PathVariable Long id, @RequestBody PermissionRequest request) {
        roleService.assignPermissions(id, request.getPermissionIds());
        return Result.success();
    }

    @Data
    public static class PermissionRequest {
        private List<Long> permissionIds;
    }
}

