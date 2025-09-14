package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.SelectionRecord;

import java.util.List;

public interface CourseDao {
    // 获取所有课程
    List<Course> getAllCourses();

    // 根据学生ID获取已选课程
    List<Course> getCoursesByStudentId(String studentId);

    // 根据教师ID获取授课课程
    List<Course> getCoursesByTeacherId(String teacherId);

    // 选课
    int selectCourse(String studentId, String courseId);

    // 退课
    int dropCourse(String studentId, String courseId);

    // 更新课程信息
    int updateCourse(Course course);

    // 添加课程
    int addCourse(Course course);

    // 删除课程
    int deleteCourse(String courseId);

    // 获取课程详情
    Course getCourseById(String courseId);

    // 获取选课记录
    List<SelectionRecord> getSelectionRecords(String courseId);

    List<Course> getCoursesByName(String keyword);

    List<Course> getCourseSchedule(String studentId, String semester);
}