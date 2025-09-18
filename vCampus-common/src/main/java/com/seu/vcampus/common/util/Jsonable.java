package com.seu.vcampus.common.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public interface Jsonable {

    // 支持的日期格式（按优先级）
    String[] DATE_FORMAT_PATTERNS = {
            "MMM d, yyyy",      // 9月 17, 2025（中文）
            "MMM d, yy",
            "yyyy-MM-dd",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd",
            "dd/MM/yyyy",
            "MMM d, yyyy, HH:mm:ss",
            "MMM d, yyyy, hh:mm:ss a",
            "MMM d, yyyy, h:mm:ss a"
    };

    // 自定义 Date 反序列化器
    class DateDeserializer implements JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            String dateStr = json.getAsString();

            dateStr = dateStr.replace(' ', ' ');

            for (String pattern : DATE_FORMAT_PATTERNS) {
                try {
                    Locale locale = pattern.contains("MMM") ? Locale.ENGLISH : Locale.CHINESE;
                    SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
                    sdf.setLenient(true); // 设置宽松解析模式
                    return sdf.parse(dateStr);
                } catch (ParseException e) {
                    // 尝试下一个格式
                    continue;
                }
            }
            // 如果所有格式都失败，尝试使用额外的英文格式
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy, hh:mm:ss a", Locale.ENGLISH);
                sdf.setLenient(true);
                return sdf.parse(dateStr);
            } catch (ParseException e) {
                // 忽略，继续抛出异常
            }
            throw new JsonParseException("无法解析日期字符串: " + dateStr);
        }
    }

    // 自定义 Integer 反序列化器：支持 Double → Integer
    class DoubleToIntDeserializer implements JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                if (json.isJsonNull()) return null;
                return (int) json.getAsDouble();
            } catch (Exception e) {
                throw new JsonParseException("无法将 " + json + " 转为 Integer", e);
            }
        }
    }



    // ✅ 增强版 Gson 实例（关键修改！）
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new DateDeserializer())
            .registerTypeAdapter(Integer.class, new DoubleToIntDeserializer())
            .registerTypeAdapter(int.class, new DoubleToIntDeserializer())
            // 可选：设置时区或默认日期格式
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    /**
     * 实现Jsonable接口的类将自己转换成JSON字符串
     * @return JSON字符串
     */
    default String toJson() {
        return gson.toJson(this);
    }

    /**
     * 将任意对象转换成JSON字符串
     * @param obj 任意对象
     * @return JSON字符串
     */
    static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * JSON字符串转成类对象（简单类型）
     * @param json JSON字符串
     * @param clazz 类型
     * @return 对象
     * @param <T> 类型
     */
    static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            System.err.println("JSON解析失败 (Class): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * JSON字符串转成复杂泛型类型（如 List<T>, Map<K,V> 等）
     * @param json JSON字符串
     * @param type 类型（使用 new TypeToken<T>(){}.getType()）
     * @return 对象
     * @param <T> 类型
     */
    static <T> T fromJson(String json, Type type) {
        if (json == null || json.trim().isEmpty()) return null;
        try {
            return gson.fromJson(json, type);
        } catch (Exception e) {
            System.err.println("JSON解析失败 (Type): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /*
    使用示例：
    List<BorrowRecord> list = Jsonable.fromJson(
        json,
        new TypeToken<List<BorrowRecord>>(){}.getType()
    );
     */
}