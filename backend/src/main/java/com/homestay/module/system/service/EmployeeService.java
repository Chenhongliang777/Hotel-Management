package com.homestay.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homestay.module.system.entity.Employee;

public interface EmployeeService extends IService<Employee> {
    
    Employee login(String username, String password);
    
    IPage<Employee> getEmployeePage(Integer page, Integer size, String keyword, String role);
    
    boolean addEmployee(Employee employee);
    
    boolean updateEmployee(Employee employee);
    
    boolean deleteEmployee(Long id);
    
    boolean updatePassword(Long id, String oldPassword, String newPassword);
}
