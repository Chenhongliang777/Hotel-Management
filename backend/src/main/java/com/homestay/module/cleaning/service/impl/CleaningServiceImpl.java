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
        // 如果有roomId但没有roomNumber，获取房间信息
        if (task.getRoomId() != null && task.getRoomNumber() == null) {
            Room room = roomMapper.selectById(task.getRoomId());
            if (room != null) {
                task.setRoomNumber(room.getRoomNumber());
            }
        }
        
        if (task.getStatus() == null) {
            task.setStatus("pending");
        }
        if (task.getTaskType() == null) {
            task.setTaskType("日常清洁");
        }
        
        this.save(task);
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
        
        // 允许pending或assigned状态的任务开始
        if (!"assigned".equals(task.getStatus()) && !"pending".equals(task.getStatus())) {
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

    @Override
    public void cancelPendingTasksByRoomId(Long roomId) {
        // 取消该房间所有待分配的清洁任务
        LambdaQueryWrapper<CleaningTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CleaningTask::getRoomId, roomId)
               .eq(CleaningTask::getStatus, "pending");
        
        List<CleaningTask> pendingTasks = this.list(wrapper);
        for (CleaningTask task : pendingTasks) {
            task.setStatus("cancelled");
            this.updateById(task);
        }
    }
}
