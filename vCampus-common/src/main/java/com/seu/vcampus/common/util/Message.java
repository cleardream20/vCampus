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
    public static final String LOGIN = "LOGIN";
    public static final String REGISTER = "REGISTER";
    public static final String LOGOUT = "LOGOUT";
    public static final String RESPONSE = "RESPONSE";
    public static final String ENTER_USER_CENTER = "ENTER_USER_CENTER";
    public static final String CONTROL_STUDENT =  "CONTROL_STUDENT";
    public static final String SELECT_COURSE = "SELECT_COURSE";

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR = "ERROR";

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private String type;
    private String status;
    private Object data;
    private String message; // 帮助信息

    public Message(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public static Message success(String type, Object data, String message) {
//        return new Message(type, STATUS_SUCCESS, data);
        return new Message(type, STATUS_SUCCESS, Jsonable.toJson(data), message);
    }

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
        return gson.fromJson(json, Message.class);
    }

    public static Message fromData(String type, boolean success, Object data, String message) {
        if(success) {
            return Message.success(type, data, message);
        }
        return Message.error(type, message);
    }

    public boolean isSuccess() {
        return STATUS_SUCCESS.equals(status);
    }
}