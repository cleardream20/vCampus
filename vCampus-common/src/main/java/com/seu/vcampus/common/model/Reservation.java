package com.seu.vcampus.common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 预约记录实体类
 * 对应数据库中的 tblReservation 表
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long reserveId;     // 预约ID
    private String userId;      // 用户ID
    private String bookIsbn;    // 图书ISBN
    private Date reserveDate;   // 预约日期
    private String status;      // 状态 (ACTIVE/CANCELLED/EXPIRED/NOTIFIED)
    private Date notifyTime;    // 通知时间
    private Date expirationDate; // 过期时间

    // 无参构造函数
    public Reservation() {
    }

    // 带参数的构造函数
    public Reservation(String userId, String bookIsbn) {
        this.userId = userId;
        this.bookIsbn = bookIsbn;
        this.reserveDate = new Date();
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public Long getReserveId() {
        return reserveId;
    }

    public void setReserveId(Long reserveId) {
        this.reserveId = reserveId;
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

    public Date getReserveDate() {
        return reserveDate;
    }

    public void setReserveDate(Date reserveDate) {
        this.reserveDate = reserveDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(Date notifyTime) {
        this.notifyTime = notifyTime;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    // 判断预约是否有效
    public boolean isValid() {
        return "ACTIVE".equals(status) || "NOTIFIED".equals(status);
    }

    // 判断是否已过期
    public boolean isExpired() {
        if (expirationDate == null) return false;
        return new Date().after(expirationDate);
    }

    // 计算剩余有效天数
    public int getRemainingDays() {
        if (expirationDate == null || !isValid()) return 0;

        long diff = expirationDate.getTime() - new Date().getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reserveId=" + reserveId +
                ", userId='" + userId + '\'' +
                ", bookIsbn='" + bookIsbn + '\'' +
                ", reserveDate=" + reserveDate +
                ", status='" + status + '\'' +
                ", notifyTime=" + notifyTime +
                '}';
    }
}