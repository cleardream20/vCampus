package com.seu.vcampus.common.model.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSchedule implements Serializable {
    private String studentId; // 学生ID
    private String studentName; // 学生姓名（可选）
    private String semester; // 学期（如：2023-2024学年第一学期）
    private List<Course> courses; // 课程列表

    // 带学生ID的构造器
    public CourseSchedule(String studentId) {
        this.studentId = studentId;
    }

    // 带学生ID和学期的构造器
    public CourseSchedule(String studentId, String semester) {
        this.studentId = studentId;
        this.semester = semester;
    }

    // 添加课程方法
    public void addCourse(Course course) {
        if (courses == null) {
            courses = new ArrayList<>();
        }
        courses.add(course);
    }

    // 重写equals和hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseSchedule that = (CourseSchedule) o;
        return Objects.equals(studentId, that.studentId) &&
                Objects.equals(semester, that.semester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, semester);
    }

    // 重写toString
    @Override
    public String toString() {
        return "CourseSchedule{" +
                "studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", semester='" + semester + '\'' +
                ", courses=" + courses +
                '}';
    }
}