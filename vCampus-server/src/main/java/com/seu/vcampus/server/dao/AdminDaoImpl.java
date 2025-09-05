package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.CourseSelectionRule;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AdminDaoImpl implements AdminDao {
    // 使用线程安全的存储结构
    private final ConcurrentMap<String, Course> courses = new ConcurrentHashMap<>();
    private volatile CourseSelectionRule currentRule = new CourseSelectionRule();

    @Override
    public void addCourse(Course course) {
        // 检查课程ID是否已存在
        if (course.getCourseId() == null || course.getCourseId().isEmpty()) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        courses.putIfAbsent(course.getCourseId(), course);
    }

    @Override
    public void updateCourse(Course course) {
        // 验证课程是否存在
        if (course.getCourseId() == null || !courses.containsKey(course.getCourseId())) {
            throw new IllegalArgumentException("课程不存在");
        }
        courses.put(course.getCourseId(), course);
    }

    @Override
    public void deleteCourse(String courseId) {
        // 验证课程是否存在
        if (courseId == null || !courses.containsKey(courseId)) {
            throw new IllegalArgumentException("课程不存在");
        }
        courses.remove(courseId);
    }

    @Override
    public void configureRule(CourseSelectionRule rule) {
        // 参数验证
        if (rule == null) {
            throw new IllegalArgumentException("规则不能为null");
        }
        if (rule.getBatchName() == null || rule.getBatchName().isEmpty()) {
            throw new IllegalArgumentException("批次名称不能为空");
        }
        this.currentRule = rule;
    }

    @Override
    public CourseSelectionRule getRule() {
        // 返回当前规则的防御性拷贝
        return new CourseSelectionRule(currentRule);
    }
}