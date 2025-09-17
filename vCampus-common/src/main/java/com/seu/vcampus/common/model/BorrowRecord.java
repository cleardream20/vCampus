package com.seu.vcampus.common.model;


import java.util.Date;

import com.seu.vcampus.common.util.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
/*
  借阅记录实体类
  对应数据库中的 tblBorrowRecord 表
 */
public class BorrowRecord implements Serializable, Jsonable {
    private Long recordId;       // 记录ID
    private String userId;       // 用户ID
    private String bookIsbn;     // 图书ISBN
    private String bookTitle;    //书本名称
    private Date borrowDate;    // 借出日期
    private Date dueDate;        // 应还日期
    private Date returnDate;     // 实际归还日期
    private String status;       // 状态 (BORROWED/RETURNED/OVERDUE)
    private Double fineAmount;  // 逾期费用
    private Integer renewalCount; // 续借次数

}