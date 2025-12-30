package com.homestay.module.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("inventory_record")
public class InventoryRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long itemId;

    private String type;

    private Integer quantity;

    private Integer beforeQuantity;

    private Integer afterQuantity;

    private Long roomId;

    private Long orderId;

    private String remark;

    private Long operatorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
