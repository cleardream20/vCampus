package com.seu.vcampus.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
//    public static final String STATUS_SUCCESS = "SUCCESS";
//    public static final String STATUS_ERROR = "ERROR";

    public static final String TYPE_LOGIN = "LOGIN";
    public static final String TYPE_REGISTER = "REGISTER";
    public static final String TYPE_RESPONSE = "RESPONSE";

    private String type;
//    private String status;
    private String data; // String instead of Obj, less "toString()" needed
                         // So toString() method is necessary

    private boolean success; // 是否成功（用于响应）
    private String message; // 提示信息

    //很标准，AI就行了

    public Message(String type, String data) {
        this.type = type;
        this.data = data;
    }

    // 工厂方法
    // e.g. return Message.success(Message.TYPE_LOGIN, user);
    public static Message success(String type, Object data) {
        return new Message(type, Jsonable.toJson(data), true, "OK");
    }

    public static Message error(String type, String errorMsg) {
        return new Message(type, null, false, errorMsg);
    }

    public static Message of(String type, Object data) {
        return new Message(type, Jsonable.toJson(data), true, "OK");
    }

    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public static Message fromJson(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, Message.class);
    }

    public static Message fromData(String type, String data, boolean success, String message) {
        return new Message(type, data, success, message);
    }

    public boolean isSuccess() {
        return success;
    }
}