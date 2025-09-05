package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.CourseSelectionRule;
import com.seu.vcampus.common.model.SelectionRecord;

import java.time.LocalDateTime;
import java.util.*;

public class CourseDaoImpl implements CourseDao {
    private final Map<String, Course> courses = new HashMap<>();
    private final Map<String, List<SelectionRecord>> selectionRecords = new HashMap<>();
    private final CourseSelectionRule rule = new CourseSelectionRule();

    public CourseDaoImpl() {
        initializeMockData();
    }

    private void initializeMockData() {
        // 初始化选课规则
        rule.setBatchName("2023秋季选课");
        rule.setStartTime(LocalDateTime.parse("2023-09-01T00:00:00"));
        rule.setEndTime(LocalDateTime.parse("2023-09-30T23:59:59"));
        rule.setMaxCredits(30);
        rule.setPrerequisites("无");
        rule.setConflictCheck(true);

        // 初始化课程数据
        Course course1 = new Course("C001", "高等数学", "T001", "数学系", 4, "周一 1-2节", "教一101", 100, 80, "无");
        Course course2 = new Course("C002", "大学英语", "T002", "外语系", 3, "周二 3-4节", "教二201", 80, 75, "无");
        Course course3 = new Course("C003", "程序设计", "T003", "计算机系", 4, "周三 5-6节", "计算中心301", 60, 55, "无");
        Course course4 = new Course("C004", "线性代数", "T001", "数学系", 3, "周四 1-2节", "教一102", 100, 85, "高等数学");

        courses.put(course1.getCourseId(), course1);
        courses.put(course2.getCourseId(), course2);
        courses.put(course3.getCourseId(), course3);
        courses.put(course4.getCourseId(), course4);

        // 初始化选课记录
        List<SelectionRecord> records1 = new ArrayList<>();
        records1.add(new SelectionRecord("20230001", "C001", LocalDateTime.parse("2023-09-10T00:00:00"), null));
        records1.add(new SelectionRecord("20230002", "C001", LocalDateTime.parse("2023-09-10T00:00:00"), null));
        selectionRecords.put("C001", records1);

        List<SelectionRecord> records2 = new ArrayList<>();
        records2.add(new SelectionRecord("20230001", "C002", LocalDateTime.parse("2023-09-11T00:00:00"), null));
        selectionRecords.put("C002", records2);
    }

    @Override
    public List<Course> getAllCourses() {
        return new ArrayList<>(courses.values());
    }

    @Override
    public List<Course> getCoursesByStudentId(String studentId) {
        List<Course> result = new ArrayList<>();
        for (List<SelectionRecord> records : selectionRecords.values()) {
            for (SelectionRecord record : records) {
                if (record.getStudentId().equals(studentId)) {
                    Course course = courses.get(record.getCourseId());
                    if (course != null) {
                        result.add(course);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<Course> getCoursesByTeacherId(String teacherId) {
        List<Course> result = new ArrayList<>();
        for (Course course : courses.values()) {
            if (course.getTeacherId().equals(teacherId)) {
                result.add(course);
            }
        }
        return result;
    }

    @Override
    public int selectCourse(String studentId, String courseId) {
        Course course = courses.get(courseId);
        if (course == null) {
            return 0;
        }

        if (course.getSelectedNum() >= course.getCapacity()) {
            return 0;
        }

        selectionRecords.putIfAbsent(courseId, new ArrayList<>());
        List<SelectionRecord> records = selectionRecords.get(courseId);

        for (SelectionRecord record : records) {
            if (record.getStudentId().equals(studentId)) {
                return 0;
            }
        }

        records.add(new SelectionRecord(studentId, courseId, LocalDateTime.now(), null));
        course.setSelectedNum(course.getSelectedNum() + 1);
        return 1;
    }

    @Override
    public int dropCourse(String studentId, String courseId) {
        List<SelectionRecord> records = selectionRecords.get(courseId);
        if (records == null) {
            return 0;
        }

        SelectionRecord toRemove = null;
        for (SelectionRecord record : records) {
            if (record.getStudentId().equals(studentId)) {
                toRemove = record;
                break;
            }
        }

        if (toRemove != null) {
            records.remove(toRemove);
            Course course = courses.get(courseId);
            if (course != null) {
                course.setSelectedNum(course.getSelectedNum() - 1);
            }
            return 1;
        }
        return 0;
    }

    @Override
    public int updateCourse(Course course) {
        if (courses.containsKey(course.getCourseId())) {
            courses.put(course.getCourseId(), course);
            return 1;
        }
        return 0;
    }

    @Override
    public int addCourse(Course course) {
        if (!courses.containsKey(course.getCourseId())) {
            courses.put(course.getCourseId(), course);
            return 1;
        }
        return 0;
    }

    @Override
    public int deleteCourse(String courseId) {
        if (courses.containsKey(courseId)) {
            courses.remove(courseId);
            selectionRecords.remove(courseId);
            return 1;
        }
        return 0;
    }

    @Override
    public Course getCourseById(String courseId) {
        return courses.get(courseId);
    }

    @Override
    public List<SelectionRecord> getSelectionRecords(String courseId) {
        return selectionRecords.getOrDefault(courseId, new ArrayList<>());
    }

    @Override
    public CourseSelectionRule getRule() {
        return rule;
    }

    @Override
    public int configureRule(CourseSelectionRule rule) {
        this.rule.setBatchName(rule.getBatchName());
        this.rule.setStartTime(rule.getStartTime());
        this.rule.setEndTime(rule.getEndTime());
        this.rule.setMaxCredits(rule.getMaxCredits());
        this.rule.setPrerequisites(rule.getPrerequisites());
        this.rule.setConflictCheck(rule.getConflictCheck());
        return 1;
    }

    @Override
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("选课统计报表\n");
        report.append("============================\n");
        report.append("总课程数: ").append(courses.size()).append("\n");

        int totalStudents = 0;
        int maxStudents = 0;
        String popularCourse = "无";

        for (Course course : courses.values()) {
            totalStudents += course.getSelectedNum();
            if (course.getSelectedNum() > maxStudents) {
                maxStudents = course.getSelectedNum();
                popularCourse = course.getCourseName();
            }
        }

        report.append("总选课人数: ").append(totalStudents).append("\n");
        report.append("最热门课程: ").append(popularCourse).append(" (").append(maxStudents).append("人)\n");
        report.append("平均每门课程选课人数: ").append(String.format("%.2f", totalStudents / (double) courses.size())).append("\n");
        report.append("============================\n");

        return report.toString();
    }
}