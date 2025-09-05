package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.CourseSelectionRule;

public interface AdminDao {
    void addCourse(Course course);
    void updateCourse(Course course);
    void deleteCourse(String courseId);
    void configureRule(CourseSelectionRule rule);
    CourseSelectionRule getRule();
}