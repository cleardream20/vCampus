package com.seu.vcampus.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String LOGIN = "LOGIN";
    public static final String REGISTER = "REGISTER";
    public static final String LOGOUT = "LOGOUT";
    public static final String ENTER_USER_CENTER = "ENTER_USER_CENTER";
    public static final String CONTROL_STUDENT =  "CONTROL_STUDENT";
    public static final String SELECT_COURSE = "SELECT_COURSE";

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR = "ERROR";

    private String type;
    private String status;
    private Object data;
    private String message; // 帮助信息

    public static Message success(String type, Object data) {
//        return new Message(type, STATUS_SUCCESS, data);
        return new Message(type, STATUS_SUCCESS, Jsonable.toJson(data), "OK");
    }

    public static Message error(String type, String error) {
        return new Message(type, STATUS_ERROR, null, error);
    }

}