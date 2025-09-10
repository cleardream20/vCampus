package com.seu.vcampus.common.util;

public class ResponseCode {
    // 成功状态码
    public static final int OK = 200;              // 请求成功
    public static final int CREATED = 201;         // 创建成功

    // 客户端错误状态码
    public static final int BAD_REQUEST = 400;     // 请求无效
    public static final int UNAUTHORIZED = 401;    // 未授权
    public static final int FORBIDDEN = 403;       // 禁止访问
    public static final int NOT_FOUND = 404;       // 资源未找到

    // 服务器错误状态码
    public static final int INTERNAL_SERVER_ERROR = 500; // 服务器内部错误
    public static final int SERVICE_UNAVAILABLE = 503;   // 服务不可用

    // 业务状态码
    public static final int COURSE_FULL = 1001;          // 课程已满
    public static final int ALREADY_SELECTED = 1002;      // 课程已选
    public static final int NOT_SELECTED = 1003;          // 未选课程
}