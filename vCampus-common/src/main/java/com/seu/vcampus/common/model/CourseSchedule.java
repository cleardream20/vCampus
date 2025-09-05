package com.seu.vcampus.common.model;

import java.io.Serializable;
import java.util.List;

public class CourseSchedule implements Serializable {
    private String studentId;
    private List<Course> courses; // 学生课表

    public CourseSchedule(String studentId) {
        this.studentId = studentId;
    }
    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}