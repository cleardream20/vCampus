package com.seu.vcampus.common.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {
    private String type;
    private final Map<String, Object> data = new HashMap<>();
    private String sender;
    private int status;
    private String description;

    public Message() {}

    public Message(String type) {
        this.type = type;
    }

    public Message(String type, int status) {
        this.type = type;
        this.status = status;
    }

    // 便捷方法
    public void addData(String key, Object value) {
        data.put(key, value);
    }

    public Object getData(String key) {
        return data.get(key);
    }

    public void setLoginData(String id, String password) {
        this.type = "LOGIN";
        data.put("id", id);
        data.put("password", password);
    }

    public void setResponse(int status, String description) {
        this.status = status;
        this.description = description;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type='" + type + '\'' +
                ", data=" + data +
                ", sender='" + sender + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}
