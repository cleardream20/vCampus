package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.CourseSelectionRule;
import com.seu.vcampus.common.util.Message;

public interface CourseService {
    // 获取所有课程列表
    Message getCourseList();

    // 学生选课
    Message selectCourse(String studentId, String courseId);

    // 学生退课
    Message dropCourse(String studentId, String courseId);

    // 获取学生已选课程
    Message getSelectedCourses(String studentId);

    // 获取教师授课课程
    Message getTeachingCourses(String teacherId);

    // 添加课程
    Message addCourse(Course course);

    // 更新课程
    Message updateCourse(Course course);

    // 删除课程
    Message deleteCourse(String courseId);

    // 获取选课规则
    Message getRule();

    // 配置选课规则
    Message configureRule(CourseSelectionRule rule);

    // 生成选课报表
    Message generateReport();

    Message getCourseSchedule(String studentId);
}