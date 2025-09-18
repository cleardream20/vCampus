package com.seu.vcampus.server.service;

import com.google.gson.JsonObject;
import com.seu.vcampus.common.model.course.*;
import com.seu.vcampus.common.util.Jsonable;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.ResponseCode;
import com.seu.vcampus.server.dao.CourseDao;
import com.seu.vcampus.server.dao.CourseDaoImpl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseServiceImpl implements CourseService {
    private final CourseDao courseDao;

    public CourseServiceImpl() {
        this.courseDao = new CourseDaoImpl();
    }

    public CourseServiceImpl(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    @Override
    public Message getCourseList() {
        try {
            System.out.println("尝试获取课程列表");
            List<Course> courses = courseDao.getAllCourses();
            JsonObject data = new JsonObject();

            // 使用 Jsonable.gson 来创建 JsonElement
            data.add("courses", Jsonable.gson.toJsonTree(courses));

            Message response = Message.success(Message.GET_COURSE_LIST, "获取课程列表成功");
            response.setData(data);
            return response;
        } catch (Exception e) {
            return Message.error(Message.GET_COURSE_LIST, "获取课程列表失败: " + e.getMessage());
        }
    }

    @Override
    public Message selectCourse(String studentId, String courseId) {
        try {
            System.out.println("尝试选课");
            Course course = courseDao.getCourseById(courseId);
            if (course == null) {
                return Message.error(Message.SELECT_COURSE, "课程不存在");
            }

            if (course.getSelectedNum() >= course.getCapacity()) {
                return Message.error(Message.SELECT_COURSE, "课程已满");
            }

            List<Course> selectedCourses = courseDao.getCoursesByStudentId(studentId);
            if (selectedCourses.stream().anyMatch(c -> c.getCourseId().equals(courseId))) {
                return Message.error(Message.SELECT_COURSE, "已选过该课程");
            }

            String newCourseSchedule = course.getSchedule();
            if (newCourseSchedule != null && !newCourseSchedule.isEmpty()) {
                for (Course selectedCourse : selectedCourses) {
                    String selectedSchedule = selectedCourse.getSchedule();
                    if (selectedSchedule != null && !selectedSchedule.isEmpty()) {
                        if (hasTimeConflict(newCourseSchedule, selectedSchedule)) {
                            return Message.error(Message.SELECT_COURSE,
                                    "时间冲突：与已选课程《" + selectedCourse.getCourseName() + "》时间重叠");
                        }
                    }
                }
            }

            int result = courseDao.selectCourse(studentId, courseId);
            if (result > 0) {
                return Message.success(Message.SELECT_COURSE, "选课成功");
            } else {
                return Message.error(Message.SELECT_COURSE, "选课失败");
            }
        } catch (Exception e) {
            return Message.error(Message.SELECT_COURSE, "选课异常: " + e.getMessage());
        }
    }

    // 辅助方法：检查时间冲突
    private boolean hasTimeConflict(String schedule1, String schedule2) {
        TimeSlot slot1 = parseTimeSlot(schedule1);
        TimeSlot slot2 = parseTimeSlot(schedule2);

        if (slot1 == null || slot2 == null) {
            return false;
        }

        if (!slot1.dayOfWeek.equals(slot2.dayOfWeek)) {
            return false;
        }

        return !(slot1.endSection < slot2.startSection || slot2.endSection < slot1.startSection);
    }

    // 辅助方法：解析时间安排字符串
    private TimeSlot parseTimeSlot(String schedule) {
        if (schedule == null || schedule.trim().isEmpty()) {
            return null;
        }

        Pattern pattern = Pattern.compile("(周一|周二|周三|周四|周五|周六|周日)\\s*(\\d+)-(\\d+)节?");
        Matcher matcher = pattern.matcher(schedule);

        if (matcher.find()) {
            try {
                String dayOfWeek = matcher.group(1);
                int startSection = Integer.parseInt(matcher.group(2));
                int endSection = Integer.parseInt(matcher.group(3));
                return new TimeSlot(dayOfWeek, startSection, endSection);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // 内部类：表示时间槽
    private static class TimeSlot {
        String dayOfWeek;
        int startSection;
        int endSection;

        TimeSlot(String dayOfWeek, int startSection, int endSection) {
            this.dayOfWeek = dayOfWeek;
            this.startSection = startSection;
            this.endSection = endSection;
        }
    }

    @Override
    public Message dropCourse(String studentId, String courseId) {
        try {
            List<Course> selectedCourses = courseDao.getCoursesByStudentId(studentId);
            if (selectedCourses.stream().noneMatch(c -> c.getCourseId().equals(courseId))) {
                return Message.error(Message.DROP_COURSE, "未选该课程");
            }

            int result = courseDao.dropCourse(studentId, courseId);
            if (result > 0) {
                return Message.success(Message.DROP_COURSE, "退课成功");
            } else {
                return Message.error(Message.DROP_COURSE, "退课失败");
            }
        } catch (Exception e) {
            return Message.error(Message.DROP_COURSE, "退课异常: " + e.getMessage());
        }
    }

    @Override
    public Message dropCourseAD(String studentId, String courseId) {
        try {
            System.out.print(studentId + " " + courseId);
            int result = courseDao.dropCourse(studentId, courseId);
            if (result > 0) {
                return Message.success(Message.DROP_COURSE, "退课成功");
            } else {
                return Message.error(Message.DROP_COURSE, "退课失败");
            }
        } catch (Exception e) {
            return Message.error(Message.DROP_COURSE, "退课异常: " + e.getMessage());
        }
    }

    @Override
    public Message getSelectedCourses(String studentId) {
        try {
            List<Course> courses = courseDao.getCoursesByStudentId(studentId);

            // 创建Gson实例来处理复杂结构
            com.google.gson.Gson gson = new com.google.gson.Gson();
            JsonObject data = new JsonObject();
            data.add("courses", gson.toJsonTree(courses)); // 直接添加JSON数组，不是字符串

            Message response = Message.success(Message.GET_SELECTED_COURSES, "获取已选课程成功");
            response.setData(data);
            return response;
        } catch (Exception e) {
            return Message.error(Message.GET_SELECTED_COURSES, "获取已选课程失败: " + e.getMessage());
        }
    }

    @Override
    public Message getTeachingCourses(String teacherId) {
        try {
            List<Course> courses = courseDao.getCoursesByTeacherId(teacherId);
            JsonObject data = new JsonObject();
            data.addProperty("courses", Jsonable.toJson(courses));
            Message response = Message.success(Message.GET_TEACHING_COURSES, data,"获取授课课程成功");
            response.setData(data);
            return response;
        } catch (Exception e) {
            return Message.error(Message.GET_TEACHING_COURSES, "获取授课课程失败: " + e.getMessage());
        }
    }

    @Override
    public Message addCourse(Course course) {
        try {
            if (course.getCourseId() == null || course.getCourseId().isEmpty()) {
                return Message.error(Message.ADD_COURSE, "课程ID不能为空");
            }
            if (course.getCourseName() == null || course.getCourseName().isEmpty()) {
                return Message.error(Message.ADD_COURSE, "课程名称不能为空");
            }
            if (course.getTeacherId() == null || course.getTeacherId().isEmpty()) {
                return Message.error(Message.ADD_COURSE, "教师ID不能为空");
            }
            if (course.getTeacherName() == null || course.getTeacherName().isEmpty()) {
                return Message.error(Message.ADD_COURSE, "教师姓名不能为空");
            }
            if (course.getSchedule() == null || course.getSchedule().isEmpty()) {
                return Message.error(Message.ADD_COURSE, "时间安排不能为空");
            }
            if (course.getLocation() == null || course.getLocation().isEmpty()) {
                return Message.error(Message.ADD_COURSE, "上课地点不能为空");
            }

            if (courseDao.getCourseById(course.getCourseId()) != null) {
                return Message.error(Message.ADD_COURSE, "课程ID已存在");
            }

            if (course.getSelectedNum() == null || course.getSelectedNum() < 0) {
                course.setSelectedNum(0);
            }
            if (course.getCapacity() == null || course.getCapacity() <= 0) {
                course.setCapacity(30);
            }
            if (course.getCredit() == null || course.getCredit() <= 0) {
                course.setCredit(2);
            }
            if (course.getStartWeek() == null || course.getStartWeek() <= 0) {
                course.setStartWeek(1);
            }
            if (course.getEndWeek() == null || course.getEndWeek() <= 0) {
                course.setEndWeek(16);
            }

            int result = courseDao.addCourse(course);
            if (result > 0) {
                return Message.success(Message.ADD_COURSE, "课程添加成功");
            } else {
                return Message.error(Message.ADD_COURSE, "课程添加失败");
            }
        } catch (Exception e) {
            return Message.error(Message.ADD_COURSE, "课程添加异常: " + e.getMessage());
        }
    }

    @Override
    public Message updateCourse(Course course) {
        try {
            if (course.getCourseId() == null || course.getCourseId().isEmpty()) {
                return Message.error(Message.UPDATE_COURSE, "课程ID不能为空");
            }

            if (courseDao.getCourseById(course.getCourseId()) == null) {
                return Message.error(Message.UPDATE_COURSE, "课程不存在");
            }

            int result = courseDao.updateCourse(course);
            if (result > 0) {
                return Message.success(Message.UPDATE_COURSE, "课程更新成功");
            } else {
                return Message.error(Message.UPDATE_COURSE, "课程更新失败");
            }
        } catch (Exception e) {
            return Message.error(Message.UPDATE_COURSE, "课程更新异常: " + e.getMessage());
        }
    }

    @Override
    public Message deleteCourse(String courseId) {
        try {
            if (courseDao.getCourseById(courseId) == null) {
                return Message.error(Message.DELETE_COURSE, "课程不存在");
            }

            int result = courseDao.deleteCourse(courseId);
            if (result > 0) {
                return Message.success(Message.DELETE_COURSE, "课程删除成功");
            } else {
                return Message.error(Message.DELETE_COURSE, "课程删除失败");
            }
        } catch (Exception e) {
            return Message.error(Message.DELETE_COURSE, "课程删除异常: " + e.getMessage());
        }
    }

    @Override
    public Message getCourseSchedule(String studentId, String semester) {
        try {
            List<Course> selectedCourses = courseDao.getCourseSchedule(studentId, semester);
            CourseSchedule schedule = new CourseSchedule(studentId);
            schedule.setCourses(selectedCourses);

            // 创建Gson实例来处理复杂结构
            com.google.gson.Gson gson = new com.google.gson.Gson();
            JsonObject data = new JsonObject();
            data.add("schedule", gson.toJsonTree(schedule)); // 直接添加JSON对象，不是字符串

            Message response = Message.success(Message.GET_COURSE_SCHEDULE, data,"获取课表成功");
            response.setData(data);
            return response;
        } catch (Exception e) {
            return Message.error(Message.GET_COURSE_SCHEDULE, "获取课表失败: " + e.getMessage());
        }
    }

    @Override
    public Message getCourseById(String keyword) {
        try {
            Course course = courseDao.getCourseById(keyword);
            if (course != null) {
                JsonObject data = new JsonObject();
                data.addProperty("course", Jsonable.toJson(course));
                Message response = Message.success(Message.GET_COURSE_BY_ID, data,"查询课程成功");
                response.setData(data);
                return response;
            } else {
                return Message.error(Message.GET_COURSE_BY_ID, "未找到课程ID为 " + keyword + " 的课程");
            }
        } catch (Exception e) {
            return Message.error(Message.GET_COURSE_BY_ID, "查询课程异常: " + e.getMessage());
        }
    }

    @Override
    public Message getCourseByName(String keyword) {
        try {
            List<Course> courses = courseDao.getCoursesByName(keyword);
            if (courses != null && !courses.isEmpty()) {
                JsonObject data = new JsonObject();
                data.addProperty("courses", Jsonable.toJson(courses));
                Message response = Message.success(Message.GET_COURSE_BY_NAME, data,"查询课程成功");
                response.setData(data);
                return response;
            } else {
                return Message.error(Message.GET_COURSE_BY_NAME, "未找到包含 '" + keyword + "' 的课程");
            }
        } catch (Exception e) {
            return Message.error(Message.GET_COURSE_BY_NAME, "查询课程异常: " + e.getMessage());
        }
    }

    @Override
    public Message getSelectionRecords(String courseId) {
        try {
            Course course = courseDao.getCourseById(courseId);
            if (course == null) {
                return Message.error(Message.GET_SELECTION_RECORDS, "课程不存在");
            }

            List<SelectionRecord> records = courseDao.getSelectionRecords(courseId);

            System.out.println("records: " + records);

            JsonObject data = new JsonObject();
            data.addProperty("records", Jsonable.toJson(records));
            Message response = Message.success(Message.GET_SELECTION_RECORDS, data,"获取选课记录成功");
            response.setData(data);
            return response;
        } catch (Exception e) {
            return Message.error(Message.GET_SELECTION_RECORDS, "获取选课记录失败: " + e.getMessage());
        }
    }
}