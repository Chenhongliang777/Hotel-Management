package com.homestay.module.system.controller;

import com.homestay.common.result.Result;
import com.homestay.module.system.entity.Permission;
import com.homestay.module.system.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "权限管理")
@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "获取权限树")
    @GetMapping("/tree")
    public Result<List<Permission>> getPermissionTree() {
        List<Permission> tree = permissionService.getPermissionTree();
        return Result.success(tree);
    }

    @Operation(summary = "获取角色的权限列表")
    @GetMapping("/role/{roleId}")
    public Result<List<Permission>> getPermissionsByRoleId(@PathVariable Long roleId) {
        List<Permission> permissions = permissionService.getPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }
}

