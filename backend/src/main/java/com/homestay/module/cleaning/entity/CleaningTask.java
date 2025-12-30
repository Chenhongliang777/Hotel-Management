package com.homestay.module.cleaning.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("cleaning_task")
public class CleaningTask implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long roomId;

    private String roomNumber;

    private String taskType;

    private String status;

    private Long assigneeId;

    private String assigneeName;

    private LocalDateTime assignTime;

    private LocalDateTime startTime;

    private LocalDateTime finishTime;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
