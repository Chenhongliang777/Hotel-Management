package com.homestay.module.pos.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pos_record")
public class PosRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String orderNo;

    private Long guestId;

    private Long roomId;

    private Long itemId;

    private String itemName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private String category;

    private String remark;

    private Long operatorId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
