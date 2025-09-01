package com.seu.vcampus.server;

import com.seu.vcampus.common.model.Course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DataManager {
    private static final List<Course> courses = new ArrayList<>();
    private static final Map<String, List<String>> studentCourses = new HashMap<>();

    public static void initialize() {
        // 创建模拟课程
        createMockCourses();

        // 创建模拟学生选课记录
        createMockSelections();
    }

    private static void createMockCourses() {
        Course course1 = new Course();
        course1.setCourseId("CS101");
        course1.setCourseName("计算机科学导论");
        course1.setCredit(3);
        course1.setCapacity(100);
        course1.setTeacherId("T001");
        course1.setSchedule("周一 8:00-9:40");

        Course course2 = new Course();
        course2.setCourseId("MA201");
        course2.setCourseName("高等数学");
        course2.setCredit(4);
        course2.setCapacity(150);
        course2.setTeacherId("T002");
        course2.setSchedule("周二 10:00-11:40");

        Course course3 = new Course();
        course3.setCourseId("PH301");
        course3.setCourseName("大学物理");
        course3.setCredit(4);
        course3.setCapacity(120);
        course3.setTeacherId("T003");
        course3.setSchedule("周三 13:30-15:10");

        Course course4 = new Course();
        course4.setCourseId("EN401");
        course4.setCourseName("大学英语");
        course4.setCredit(2);
        course4.setCapacity(80);
        course4.setTeacherId("T004");
        course4.setSchedule("周四 15:30-17:10");

        Course course5 = new Course();
        course5.setCourseId("SE501");
        course5.setCourseName("软件工程");
        course5.setCredit(3);
        course5.setCapacity(60);
        course5.setTeacherId("T005");
        course5.setSchedule("周五 8:00-9:40");

        courses.add(course1);
        courses.add(course2);
        courses.add(course3);
        courses.add(course4);
        courses.add(course5);
    }

    private static void createMockSelections() {
        // 创建模拟学生
        studentCourses.put("20210001", new ArrayList<>());
        studentCourses.put("20210002", new ArrayList<>());
        studentCourses.put("20210003", new ArrayList<>());

        // 随机分配选课
        Random random = new Random();
        for (Course course : courses) {
            int selected = random.nextInt(course.getCapacity() / 2) + 1;
            course.setSelected(selected);

            // 随机分配学生选课
            for (int i = 0; i < selected; i++) {
                String studentId = "2021000" + (random.nextInt(3) + 1);
                if (!studentCourses.get(studentId).contains(course.getCourseId())) {
                    studentCourses.get(studentId).add(course.getCourseId());
                }
            }
        }
    }

    public static List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    public static List<Course> getSelectedCourses(String studentId) {
        List<Course> selected = new ArrayList<>();
        List<String> courseIds = studentCourses.getOrDefault(studentId, new ArrayList<>());

        for (String courseId : courseIds) {
            for (Course course : courses) {
                if (course.getCourseId().equals(courseId)) {
                    selected.add(course);
                    break;
                }
            }
        }
        return selected;
    }

    public static boolean selectCourse(String studentId, String courseId) {
        // 检查学生是否存在
        if (!studentCourses.containsKey(studentId)) {
            return false;
        }

        // 检查课程是否存在
        Course targetCourse = null;
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId)) {
                targetCourse = course;
                break;
            }
        }
        if (targetCourse == null) {
            return false;
        }

        // 检查是否已选
        if (studentCourses.get(studentId).contains(courseId)) {
            return false;
        }

        // 检查课程是否已满
        if (targetCourse.getSelected() >= targetCourse.getCapacity()) {
            return false;
        }

        // 添加选课记录
        studentCourses.get(studentId).add(courseId);
        targetCourse.setSelected(targetCourse.getSelected() + 1);
        return true;
    }

    public static boolean dropCourse(String studentId, String courseId) {
        // 检查学生是否存在
        if (!studentCourses.containsKey(studentId)) {
            return false;
        }

        // 检查课程是否存在
        Course targetCourse = null;
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId)) {
                targetCourse = course;
                break;
            }
        }
        if (targetCourse == null) {
            return false;
        }

        // 检查是否已选
        if (!studentCourses.get(studentId).contains(courseId)) {
            return false;
        }

        // 移除选课记录
        studentCourses.get(studentId).remove(courseId);
        targetCourse.setSelected(targetCourse.getSelected() - 1);
        return true;
    }
}