package com.seu.vcampus.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface Jsonable {
    // 全局唯一实例
    Gson gson = new GsonBuilder().create();

    /**
     * 实现Jsonable接口的类将自己转换成JSON字符串
     * @return
     */
    default String toJson() {
        return gson.toJson(this);
    }

    /**
     * 将任意类对象转化成Json字符串
     * @param obj 任意类对象
     * @return Json String
     */
    static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        return gson.toJson(obj);
    }

    /**
     * JSON字符串转回成类对象
     * @param json JSON字符串
     * @param clazz 类
     * @return 类对象
     * @param <T> 类
     */
    // 反射机制 java.lang.reflect.Type type + TypeToken
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

    /**
     * 进阶fromJson，支持复杂泛型类型
     * @param json JSON字符串
     * @param type 类型
     * @return 复杂类型对象
     * @param <T> 类型
     */
    static <T> T fromJson(String json, java.lang.reflect.Type type) {
        if (json == null || json.trim().isEmpty()) return null;
        try {
            return gson.fromJson(json, type);
        } catch (Exception e) {
            System.err.println("JSON解析失败: " + e.getMessage());
            return null;
        }
    }

    /*
    使用示例
    List<Course> courses = Jsonable.fromJson(
        json,
        new TypeToken<T>(){}.getType() // 这里T为List>Course>
    );
     */
}
