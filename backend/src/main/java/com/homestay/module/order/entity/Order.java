package com.homestay.module.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("booking_order")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long guestId;

    private Long roomId;

    private Long roomTypeId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Integer nights;

    private Integer guestCount;

    private BigDecimal roomPrice;

    private BigDecimal totalPrice;

    private BigDecimal deposit;

    private BigDecimal paidAmount;

    private BigDecimal extraCharges;

    private String status;

    private String guestName;

    private String guestPhone;

    private String guestIdCard;

    private String remark;

    private Integer modifyCount;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private Long operatorId;

    @TableField(exist = false)
    private String roomTypeName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
