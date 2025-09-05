package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.CourseSelectionRule;
import com.seu.vcampus.common.util.Message;

public interface AdminService {
    Message addCourse(Course course);
    Message updateCourse(Course course);
    Message deleteCourse(String courseId);
    Message configureRule(CourseSelectionRule rule);
    Message getRule();
    Message generateReport();
}