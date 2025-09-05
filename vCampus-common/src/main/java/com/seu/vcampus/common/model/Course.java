package com.seu.vcampus.common.model;

import java.io.Serializable;

public class Course implements Serializable {
    private String courseId;
    private String courseName;
    private String teacherId; // 使用教师ID而不是姓名
    private String department;
    private Integer credit;
    private String time; // 格式: "周一 1-2节"
    private String location;
    private Integer capacity;
    private Integer selectedNum;
    private String prerequisites; // 先修课程要求

    // 默认构造函数
    public Course() {}

    // 带参数的构造函数
    public Course(String courseId, String courseName, String teacherId, String department,
                  Integer credit, String time, String location, Integer capacity,
                  Integer selectedNum, String prerequisites) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherId = teacherId;
        this.department = department;
        this.credit = credit;
        this.time = time;
        this.location = location;
        this.capacity = capacity;
        this.selectedNum = selectedNum;
        this.prerequisites = prerequisites;
    }

    // Getters and Setters
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getCredit() { return credit; }
    public void setCredit(Integer credit) { this.credit = credit; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getSelectedNum() { return selectedNum; }
    public void setSelectedNum(Integer selectedNum) { this.selectedNum = selectedNum; }

    public String getPrerequisites() { return prerequisites; }
    public void setPrerequisites(String prerequisites) { this.prerequisites = prerequisites; }
}