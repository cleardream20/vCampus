package com.seu.vcampus.common.model;


import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
/*
  预约记录实体类
  对应数据库中的 tblReservation 表
 */
public class Reservation implements Serializable {
    private Long reserveId;     // 预约ID
    private String userId;      // 用户ID
    private String bookIsbn;    // 图书ISBN
    private String bookTitle;   // 图书标题
    private Date reserveDate;   // 预约日期
    private String status;      // 状态 (PENDING/ACTIVE/CANCELLED/EXPIRED)
}