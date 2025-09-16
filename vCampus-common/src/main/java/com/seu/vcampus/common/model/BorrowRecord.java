package com.seu.vcampus.common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 借阅记录实体类
 * 对应数据库中的 tblBorrowRecord 表
 */
public class BorrowRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long recordId;       // 记录ID
    private String userId;       // 用户ID
    private String bookIsbn;     // 图书ISBN
    private Date borrowDate;    // 借出日期
    private Date dueDate;        // 应还日期
    private Date returnDate;     // 实际归还日期
    private String status;       // 状态 (BORROWED/RETURNED/OVERDUE)
    private Double fineAmount;  // 逾期费用
    private Integer renewalCount; // 续借次数

    // 无参构造函数
    public BorrowRecord() {
    }

    // 带参数的构造函数
    public BorrowRecord(String userId, String bookIsbn, Date borrowDate, Date dueDate) {
        this.userId = userId;
        this.bookIsbn = bookIsbn;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = "BORROWED";
        this.fineAmount = 0.0;
        this.renewalCount = 0;
    }

    // Getters and Setters
    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public Integer getRenewalCount() {
        return renewalCount;
    }

    public void setRenewalCount(Integer renewalCount) {
        this.renewalCount = renewalCount;
    }

    // 判断是否逾期
    public boolean isOverdue() {
        Date now = new Date();
        return "BORROWED".equals(status) && now.after(dueDate);
    }

    // 计算逾期天数
    public int getOverdueDays() {
        if (!isOverdue()) return 0;

        long diff = new Date().getTime() - dueDate.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    @Override
    public String toString() {
        return "BorrowRecord{" +
                "recordId=" + recordId +
                ", userId='" + userId + '\'' +
                ", bookIsbn='" + bookIsbn + '\'' +
                ", borrowDate=" + borrowDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status='" + status + '\'' +
                ", fineAmount=" + fineAmount +
                '}';
    }
}