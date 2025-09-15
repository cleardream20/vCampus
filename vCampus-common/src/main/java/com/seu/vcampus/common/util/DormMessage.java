package com.seu.vcampus.common.util;

public class DormMessage {
    // 学生端消息类型
    public static final String GET_DORM_INFO = "GET_DORM_INFO";
    public static final String SUBMIT_APPLICATION = "SUBMIT_APPLICATION"; // 统一申请提交
    public static final String GET_APPLICATIONS = "GET_APPLICATIONS";
    public static final String SUBMIT_SERVICE = "SUBMIT_SERVICE"; // 统一服务申请
    public static final String GET_SERVICES = "GET_SERVICES";
    
    // 管理端消息类型
    public static final String GET_ALL_DORM_INFO = "GET_ALL_DORM_INFO";
    public static final String GET_PENDING_APPLICATIONS = "GET_PENDING_APPLICATIONS";
    public static final String UPDATE_APPLICATION_STATUS = "UPDATE_APPLICATION_STATUS";
    public static final String GET_ALL_SERVICES = "GET_ALL_SERVICES";
    public static final String UPDATE_SERVICE_STATUS = "UPDATE_SERVICE_STATUS";
    
    // 申请类型常量
    public static final String APPLICATION_CHECK_IN = "入住";
    public static final String APPLICATION_ADJUST = "调换";
    public static final String APPLICATION_CHECK_OUT = "退宿";
    
    // 申请状态常量
    public static final String APPLICATION_PENDING = "待审核";
    public static final String APPLICATION_APPROVED = "已批准";
    public static final String APPLICATION_REJECTED = "已拒绝";
    
    // 服务状态常量
    public static final String SERVICE_PENDING = "待处理";
    public static final String SERVICE_PROCESSING = "处理中";
    public static final String SERVICE_COMPLETED = "已完成";
    
    // 住宿状态常量
    public static final String STATUS_RESIDENT = "在住";
    public static final String STATUS_CHECKED_OUT = "已退宿";
}
