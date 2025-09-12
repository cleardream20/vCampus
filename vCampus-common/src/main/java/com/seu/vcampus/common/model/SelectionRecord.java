package com.seu.vcampus.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SelectionRecord implements Serializable {
    private String recordId; // 对应RecordID (自动编号)
    private String studentId; // 对应StudentID (短文本)
    private String studentName; // 对应StudentName (短文本)
    private String courseId; // 对应CourseID (短文本)
    private String courseName; // 对应CourseName (短文本)
    private LocalDateTime selectionTime; // 对应SelectionTime (日期/时间)
    private String department; // 对应Department (短文本)

    // 无参构造器
    public SelectionRecord() {}

    // 全参数构造器
    public SelectionRecord(String recordId, String studentId, String studentName,
                           String courseId, String courseName, LocalDateTime selectionTime,
                           String department) {
        this.recordId = recordId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.selectionTime = selectionTime;
        this.department = department;
    }

    // Getters and Setters
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public LocalDateTime getSelectionTime() { return selectionTime; }
    public void setSelectionTime(LocalDateTime selectionTime) { this.selectionTime = selectionTime; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return "SelectionRecord{" +
                "recordId='" + recordId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", selectionTime=" + selectionTime +
                ", department='" + department + '\'' +
                '}';
    }
}