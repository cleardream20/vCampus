package com.seu.vcampus.common.model;


import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 预约记录实体类
 * 对应数据库中的 tblReservation 表
 */
public class Reservation implements Serializable {
    private Long reserveId;     // 预约ID
    private String userId;      // 用户ID
    private String bookIsbn;    // 图书ISBN
    private Date reserveDate;   // 预约日期
    private String status;      // 状态 (ACTIVE/CANCELLED/EXPIRED/NOTIFIED)
    private Date notifyTime;    // 通知时间
    private Date expirationDate; // 过期时间

}