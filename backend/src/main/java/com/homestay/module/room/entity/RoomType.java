package com.homestay.module.room.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("room_type")
public class RoomType implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private BigDecimal basePrice;

    private BigDecimal weekendPrice;

    private BigDecimal holidayPrice;

    private Integer maxGuests;

    private Integer bedCount;

    private String bedType;

    private Double area;

    private String facilities;

    private String images;

    private Integer status;

    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
