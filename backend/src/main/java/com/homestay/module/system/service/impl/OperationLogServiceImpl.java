package com.homestay.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homestay.module.system.entity.OperationLog;
import com.homestay.module.system.mapper.OperationLogMapper;
import com.homestay.module.system.service.OperationLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Async
    @Override
    public void saveLog(OperationLog log) {
        this.save(log);
    }

    @Override
    public IPage<OperationLog> getLogPage(Integer page, Integer size, String module, String username, String startDate, String endDate) {
        Page<OperationLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(module)) {
            wrapper.eq(OperationLog::getModule, module);
        }
        
        if (StringUtils.hasText(username)) {
            wrapper.like(OperationLog::getUsername, username);
        }
        
        if (StringUtils.hasText(startDate)) {
            LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
            wrapper.ge(OperationLog::getCreateTime, start);
        }
        
        if (StringUtils.hasText(endDate)) {
            LocalDateTime end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
            wrapper.le(OperationLog::getCreateTime, end);
        }
        
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return this.page(pageParam, wrapper);
    }
}
