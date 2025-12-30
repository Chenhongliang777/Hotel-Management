package com.homestay.module.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.homestay.common.result.PageResult;
import com.homestay.common.result.Result;
import com.homestay.context.UserContextHolder;
import com.homestay.module.system.entity.Employee;
import com.homestay.module.system.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "员工管理")
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Operation(summary = "获取员工列表")
    @GetMapping("/page")
    public Result<PageResult<Employee>> getEmployeePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role) {
        IPage<Employee> pageResult = employeeService.getEmployeePage(page, size, keyword, role);
        pageResult.getRecords().forEach(e -> e.setPassword(null));
        return Result.success(PageResult.of(pageResult));
    }

    @Operation(summary = "获取员工详情")
    @GetMapping("/{id}")
    public Result<Employee> getEmployee(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            employee.setPassword(null);
        }
        return Result.success(employee);
    }

    @Operation(summary = "获取当前登录员工信息")
    @GetMapping("/current")
    public Result<Employee> getCurrentEmployee() {
        Long userId = UserContextHolder.getUserId();
        Employee employee = employeeService.getById(userId);
        if (employee != null) {
            employee.setPassword(null);
        }
        return Result.success(employee);
    }

    @Operation(summary = "添加员工")
    @PostMapping
    public Result<Void> addEmployee(@RequestBody Employee employee) {
        employeeService.addEmployee(employee);
        return Result.success();
    }

    @Operation(summary = "更新员工")
    @PutMapping
    public Result<Void> updateEmployee(@RequestBody Employee employee) {
        employeeService.updateEmployee(employee);
        return Result.success();
    }

    @Operation(summary = "删除员工")
    @DeleteMapping("/{id}")
    public Result<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return Result.success();
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody PasswordRequest request) {
        Long userId = UserContextHolder.getUserId();
        employeeService.updatePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Result.success();
    }

    @Operation(summary = "重置员工密码")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            employee.setPassword("123456");
            employeeService.updateById(employee);
        }
        return Result.success();
    }

    @Data
    public static class PasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}
