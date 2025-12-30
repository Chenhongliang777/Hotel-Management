package com.homestay.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homestay.common.exception.BusinessException;
import com.homestay.module.system.entity.Employee;
import com.homestay.module.system.mapper.EmployeeMapper;
import com.homestay.module.system.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public Employee login(String username, String password) {
        Employee employee = this.getOne(new LambdaQueryWrapper<Employee>()
                .eq(Employee::getUsername, username)
                .eq(Employee::getStatus, 1));
        
        if (employee == null) {
            throw new BusinessException("用户名或密码错误");
        }
        
        if (!password.equals(employee.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        
        return employee;
    }

    @Override
    public IPage<Employee> getEmployeePage(Integer page, Integer size, String keyword, String role) {
        Page<Employee> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Employee::getUsername, keyword)
                    .or().like(Employee::getRealName, keyword)
                    .or().like(Employee::getPhone, keyword));
        }
        
        if (StringUtils.hasText(role)) {
            wrapper.eq(Employee::getRole, role);
        }
        
        wrapper.orderByDesc(Employee::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public boolean addEmployee(Employee employee) {
        Employee existing = this.getOne(new LambdaQueryWrapper<Employee>()
                .eq(Employee::getUsername, employee.getUsername()));
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }
        
        if (!StringUtils.hasText(employee.getPassword())) {
            employee.setPassword("123456");
        }
        employee.setStatus(1);
        return this.save(employee);
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        Employee existing = this.getById(employee.getId());
        if (existing == null) {
            throw new BusinessException("员工不存在");
        }
        
        Employee sameUsername = this.getOne(new LambdaQueryWrapper<Employee>()
                .eq(Employee::getUsername, employee.getUsername())
                .ne(Employee::getId, employee.getId()));
        if (sameUsername != null) {
            throw new BusinessException("用户名已存在");
        }
        
        return this.updateById(employee);
    }

    @Override
    public boolean deleteEmployee(Long id) {
        Employee employee = this.getById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        return this.removeById(id);
    }

    @Override
    public boolean updatePassword(Long id, String oldPassword, String newPassword) {
        Employee employee = this.getById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }
        
        if (!oldPassword.equals(employee.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        
        employee.setPassword(newPassword);
        return this.updateById(employee);
    }
}
