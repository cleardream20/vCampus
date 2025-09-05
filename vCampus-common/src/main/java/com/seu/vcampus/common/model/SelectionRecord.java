package com.seu.vcampus.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SelectionRecord implements Serializable {
    private String studentId;
    private String courseId;
    private LocalDateTime selectTime;
    private Integer score;

    // 新增：日期时间格式化器
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public SelectionRecord(String studentId, String courseId, String selectTimeStr, Integer score) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.selectTime = LocalDateTime.parse(selectTimeStr + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.score = score;
    }

    // 保留原有构造方法
    public SelectionRecord(String studentId, String courseId, LocalDateTime selectTime, Integer score) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.selectTime = selectTime;
        this.score = score;
    }

    // Getters and Setters保持不变
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public LocalDateTime getSelectTime() { return selectTime; }
    public void setSelectTime(LocalDateTime selectTime) { this.selectTime = selectTime; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}