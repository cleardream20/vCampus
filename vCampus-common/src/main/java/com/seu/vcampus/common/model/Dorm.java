// Dorm.java (扩展版本)
package com.seu.vcampus.common.model;

import lombok.Data;
import java.util.Date;

@Data
public class Dorm {
    // 基本住宿信息
    private String studentId;
    private String name;
    private String college;
    private String major;
    private String building;
    private String roomNumber;
    private String bedNumber;
    private String dormType;
    private String checkInDate;
    private String expectedCheckOutDate;
    private String status;
    private String dormPhone;
    private String roommates;
    private String dormManager;
    private String managerPhone;
    
    // 申请相关字段
    private String applicationType; // 申请类型：入住、退宿、调整
    private String applicationStatus; // 申请状态：待审核、已批准、已拒绝
    private Date applicationTime; // 申请时间
    private String applicationReason; // 申请理由
    private String reviewer; // 审核人
    private String reviewRemarks; // 审核备注
    
    // 服务请求相关字段
    private String serviceType; // 服务类型：报修、投诉
    private String serviceStatus; // 服务状态：待处理、处理中、已完成
    private Date serviceTime; // 服务提交时间
    private String serviceDescription; // 服务描述
    private String serviceProcessor; // 处理人
    private Date expectedCompletionTime; // 预计完成时间
    private String serviceLocation; // 服务位置
}
