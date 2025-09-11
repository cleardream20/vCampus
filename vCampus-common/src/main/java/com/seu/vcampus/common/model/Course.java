package com.seu.vcampus.common.model;

import java.io.Serializable;

public class Course implements Serializable {
    private String courseId;
    private String courseName;
    private String teacherName;
    private String teacherId;
    private String department;
    private Integer credit;
    private String schedule; // 格式: "周一 1-2节"
    private String location;
    private Integer capacity;
    private Integer selectedNum;
    private Integer availableSlots; // 新增：可获得数量（容量-已选）
    private Integer startWeek;      // 新增：开始周数（1-20）
    private Integer endWeek;        // 新增：结束周数（1-20）

    // 默认构造函数
    public Course() {}

    // 带参数的构造函数
    public Course(String courseId, String courseName, String teacherName,String teacherId, String department,
                  Integer credit, String schedule, String location, Integer capacity,
                  Integer selectedNum, Integer startWeek, Integer endWeek) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.department = department;
        this.credit = credit;
        this.schedule = schedule;
        this.location = location;
        this.capacity = capacity;
        this.selectedNum = selectedNum;
        this.availableSlots = capacity - selectedNum; // 自动计算可获得数量
        this.startWeek = startWeek;
        this.endWeek = endWeek;
    }

    // Getters and Setters
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getCredit() { return credit; }
    public void setCredit(Integer credit) { this.credit = credit; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
        updateAvailableSlots();
    }


    public Integer getSelectedNum() { return selectedNum; }
    public void setSelectedNum(Integer selectedNum) {
        this.selectedNum = selectedNum;
        updateAvailableSlots();
    }

    public Integer getAvailableSlots() { return availableSlots; }

    public Integer getStartWeek() { return startWeek; }
    public void setStartWeek(Integer startWeek) {
        if (startWeek < 1 || startWeek > 20) {
            throw new IllegalArgumentException("开始周数必须在1-20之间");
        }
        this.startWeek = startWeek;
    }

    public Integer getEndWeek() { return endWeek; }
    public void setEndWeek(Integer endWeek) {
        if (endWeek < 1 || endWeek > 20) {
            throw new IllegalArgumentException("结束周数必须在1-20之间");
        }
        this.endWeek = endWeek;
    }

    // 私有方法：自动更新可获得数量
    private void updateAvailableSlots() {
        if (capacity != null && selectedNum != null) {
            this.availableSlots = capacity - selectedNum;
        }
    }

    // 新增：检查周数范围是否有效
    public boolean isValidWeekRange() {
        return startWeek != null && endWeek != null && startWeek <= endWeek;
    }

    // toString示例（调试用）
    @Override
    public String toString() {
        return String.format("%s %s 教师:%s(%s) 时间:%s 地点:%s 周数:%d-%d 余量:%d/%d",
                courseId,
                courseName,
                teacherName,
                teacherId,
                schedule,
                location,
                startWeek,
                endWeek,
                availableSlots,
                capacity);
    }
}