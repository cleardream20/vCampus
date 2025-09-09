package com.seu.vcampus.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface Jsonable {
    // 全局唯一实例
    Gson gson = new GsonBuilder().create();

    default String toJson() {
        return gson.toJson(this);
    }

    static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        return gson.toJson(obj);
    }

    static  <T> T fromJson(String json, Class<T> clazz) {
        if(json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            System.err.println("JSON解析失败: " + e.getMessage());
            return null;
        }
    }
}
