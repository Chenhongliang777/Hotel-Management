package com.homestay.module.cleaning.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.homestay.module.cleaning.entity.CleaningTask;

import java.util.List;

public interface CleaningService extends IService<CleaningTask> {
    
    CleaningTask createTask(CleaningTask task);
    
    IPage<CleaningTask> getTaskPage(Integer page, Integer size, String status, Long assigneeId);
    
    List<CleaningTask> getMyTasks(Long assigneeId);
    
    List<CleaningTask> getPendingTasks();
    
    boolean assignTask(Long taskId, Long assigneeId, String assigneeName);
    
    boolean startTask(Long taskId);
    
    boolean completeTask(Long taskId);
}
