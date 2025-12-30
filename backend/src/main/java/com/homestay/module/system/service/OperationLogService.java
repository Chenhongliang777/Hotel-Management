package com.homestay.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homestay.module.system.entity.OperationLog;

public interface OperationLogService extends IService<OperationLog> {
    
    void saveLog(OperationLog log);
    
    IPage<OperationLog> getLogPage(Integer page, Integer size, String module, String username, String startDate, String endDate);
}
