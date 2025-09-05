package com.seu.vcampus.common.util;

public class ResponseCode {
    // HTTP标准状态码
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL_SERVER_ERROR = 500;

    // 业务状态码（课程相关）
    public static final int COURSE_FULL = 1001;
    public static final int ALREADY_SELECTED = 1002;
    public static final int NOT_SELECTED = 1003;
    public static final int COURSE_CONFLICT = 1004;
    public static final int CREDIT_LIMIT_EXCEEDED = 1005;
    public static final int PREREQUISITE_NOT_MET = 1006;
    public static final int COURSE_NOT_FOUND = 1007;
    public static final int COURSE_ALREADY_EXISTS = 1008;

    // 用户相关
    public static final int USER_NOT_FOUND = 2001;

    // 规则配置相关
    public static final int RULE_VALIDATION_FAILED = 3001;

    // 通用业务状态
    public static final int FAIL = 9001;
    public static final int ALREADY_EXISTS = 9002;
}