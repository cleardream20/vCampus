package com.seu.vcampus.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    // 用户相关
    public static final String LOGIN = "LOGIN";
    public static final String REGISTER = "REGISTER";
    public static final String LOGOUT = "LOGOUT";
    public static final String KICKED = "KICKED";

    public static final String DELETE_USER = "DELETE_USER";
    public static final String ADD_USER = "ADD_USER";
    public static final String GET_USER = "GET_USER";
    public static final String UPDATE_USER = "UPDATE_USER";


    public static final String RESPONSE = "RESPONSE";
    public static final String ENTER_USER_CENTER = "ENTER_USER_CENTER";
    public static final String AD_STUDENT =  "AD_STUDENT";
    public static final String ST_STUDENT =  "ST_STUDENT";

    // 选课相关
    public static final String GET_COURSE_BY_ID = "GET_COURSE_BY_ID";
    public static final String DROP_COURSE_AD ="DROP_COURSE_AD" ;
    public static final String GET_COURSE_BY_NAME = "GET_COURSE_BY_NAME";
    public static final String GET_COURSE_LIST = "GET_COURSE_LIST";
    public static final String SELECT_COURSE = "SELECT_COURSE";
    public static final String DROP_COURSE = "DROP_COURSE";
    public static final String GET_SELECTED_COURSES = "GET_SELECTED_COURSES";
    public static final String GET_COURSE_SCHEDULE = "GET_COURSE_SCHEDULE";
    public static final String ADD_COURSE = "ADD_COURSE";
    public static final String UPDATE_COURSE = "UPDATE_COURSE";
    public static final String DELETE_COURSE = "DELETE_COURSE";
    public static final String GET_TEACHING_COURSES = "GET_TEACHING_COURSES";
    public static final String GET_SELECTION_RECORDS = "GET_SELECTION_RECORDS ";

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR = "ERROR";

    private static final Gson gson = new GsonBuilder().create();

    private String type;
    private String status;
    private Object data;
    private String message; // 帮助信息

    public Message(String type) {
        this.type = type;
    }

    public Message(String type, String status) {
        this.type = type;
        this.status = status;
    }

    public Message(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    // Message.success() "特殊"的构造函数，专门构造STATUS_SUCCESS的Message
    public static Message success(String type, Object data, String message) {
//        return new Message(type, STATUS_SUCCESS, data);
        return new Message(type, STATUS_SUCCESS, data, message);
    }

    public static Message success(String type, String message) {
        return new Message(type, STATUS_SUCCESS, null, message);
    }

    // Message.success() "特殊"的构造函数，专门构造STATUS_ERROR的Message
    public static Message error(String type, String error) {
        return new Message(type, STATUS_ERROR, null, error);
    }

    /**
     * 把当前Message实例转换成一个JSON字符串
     * @return tojson结果
     */
    public String toJson() {
        return gson.toJson(this);
    }

    /**
     * 将JSON还原成Message对象
     * @param json JSON字符串
     * @return fromJson 原Message对象
     */
    public static Message fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON 不能为空");
        }
        return gson.fromJson(json, Message.class);
    }

    public static Message fromData(String type, boolean success, Object data, String message) {
        return success ? success(type, data, message) : error(type, message);
    }

    public boolean isSuccess() {
        return STATUS_SUCCESS.equals(status);
    }
}