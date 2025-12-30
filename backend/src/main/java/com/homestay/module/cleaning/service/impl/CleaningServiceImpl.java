package com.homestay.module.cleaning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.homestay.common.exception.BusinessException;
import com.homestay.module.cleaning.entity.CleaningTask;
import com.homestay.module.cleaning.mapper.CleaningTaskMapper;
import com.homestay.module.cleaning.service.CleaningService;
import com.homestay.module.room.entity.Room;
import com.homestay.module.room.mapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CleaningServiceImpl extends ServiceImpl<CleaningTaskMapper, CleaningTask> implements CleaningService {

    @Autowired
    private RoomMapper roomMapper;

    @Override
    public CleaningTask createTask(CleaningTask task) {
        Room room = roomMapper.selectById(task.getRoomId());
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        
        task.setRoomNumber(room.getRoomNumber());
        task.setStatus("pending");
        this.save(task);
        
        room.setCleanStatus("dirty");
        roomMapper.updateById(room);
        
        return task;
    }

    @Override
    public IPage<CleaningTask> getTaskPage(Integer page, Integer size, String status, Long assigneeId) {
        Page<CleaningTask> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CleaningTask> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(CleaningTask::getStatus, status);
        }
        
        if (assigneeId != null) {
            wrapper.eq(CleaningTask::getAssigneeId, assigneeId);
        }
        
        wrapper.orderByDesc(CleaningTask::getCreateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public List<CleaningTask> getMyTasks(Long assigneeId) {
        return this.list(new LambdaQueryWrapper<CleaningTask>()
                .eq(CleaningTask::getAssigneeId, assigneeId)
                .in(CleaningTask::getStatus, "assigned", "in_progress")
                .orderByAsc(CleaningTask::getCreateTime));
    }

    @Override
    public List<CleaningTask> getPendingTasks() {
        return this.list(new LambdaQueryWrapper<CleaningTask>()
                .eq(CleaningTask::getStatus, "pending")
                .orderByAsc(CleaningTask::getCreateTime));
    }

    @Override
    public boolean assignTask(Long taskId, Long assigneeId, String assigneeName) {
        CleaningTask task = this.getById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        
        task.setAssigneeId(assigneeId);
        task.setAssigneeName(assigneeName);
        task.setAssignTime(LocalDateTime.now());
        task.setStatus("assigned");
        
        return this.updateById(task);
    }

    @Override
    public boolean startTask(Long taskId) {
        CleaningTask task = this.getById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        
        if (!"assigned".equals(task.getStatus())) {
            throw new BusinessException("任务状态不允许开始");
        }
        
        task.setStartTime(LocalDateTime.now());
        task.setStatus("in_progress");
        
        Room room = roomMapper.selectById(task.getRoomId());
        if (room != null) {
            room.setCleanStatus("cleaning");
            roomMapper.updateById(room);
        }
        
        return this.updateById(task);
    }

    @Override
    @Transactional
    public boolean completeTask(Long taskId) {
        CleaningTask task = this.getById(taskId);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        
        if (!"in_progress".equals(task.getStatus()) && !"assigned".equals(task.getStatus())) {
            throw new BusinessException("任务状态不允许完成");
        }
        
        task.setFinishTime(LocalDateTime.now());
        task.setStatus("completed");
        this.updateById(task);
        
        Room room = roomMapper.selectById(task.getRoomId());
        if (room != null) {
            room.setCleanStatus("clean");
            if ("maintenance".equals(room.getStatus())) {
                room.setStatus("available");
            }
            roomMapper.updateById(room);
        }
        
        return true;
    }
}
