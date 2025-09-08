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

    public Message(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public static Message fromJson(String json) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, Message.class);
    }

    public boolean isSuccess() {
        return success;
    }
}