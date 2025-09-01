package com.seu.vcampus.common.util;

import java.io.Serializable;

public class Message implements Serializable {
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_ERROR = "ERROR";

    private String type;
    private String status;
    private Object data;

    public Message() {}

    public Message(String type, String status, Object data) {
        this.type = type;
        this.status = status;
        this.data = data;
    }

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}