package com.seu.vcampus.common.model;

import com.seu.vcampus.common.util.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dorm implements Serializable, Jsonable {
    // 基本住宿信息 (对应 tblDormInfo 表)
    private String studentId;
    private String name;
    private String building;
    private String roomNumber;
    private String bedNumber;
    private Date checkInDate;
    private String status; // 在住/已退宿

    // 申请相关字段 (对应 tblDormApplication 表)
    private String applicationId;
    private String applicationType; // 入住、调换、退宿
    private Date applicationTime;
    private String applicationStatus; // 待审核、已批准、已拒绝
    private String reviewer;

    // 服务请求相关字段 (对应 tblDormService 表)
    private String serviceId;
    private String serviceDescription; // 问题描述
    private Date serviceTime;
    private String serviceStatus; // 待处理、处理中、已完成
    private String serviceProcessor;
}