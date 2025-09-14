package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.CourseSchedule;
import com.seu.vcampus.common.model.CourseSelectionRule;
import com.seu.vcampus.common.model.SelectionRecord;
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
            List<Course> courses = courseDao.getAllCourses();
            Message response = new Message(Message.GET_COURSE_LIST);
            response.setStatus(ResponseCode.OK);
            response.addData("courses", courses);
            return response;
        } catch (Exception e) {
            return createErrorResponse(Message.GET_COURSE_LIST,
                    ResponseCode.INTERNAL_SERVER_ERROR, "获取课程列表失败: " + e.getMessage());
        }
    }

    @Override
    public Message selectCourse(String studentId, String courseId) {
        try {
            // 检查课程是否存在
            Course course = courseDao.getCourseById(courseId);
            if (course == null) {
                return createErrorResponse(Message.SELECT_COURSE,
                        ResponseCode.NOT_FOUND, "课程不存在");
            }

             //检查课程是否已满
            if (course.getSelectedNum() >= course.getCapacity()) {
                return createErrorResponse(Message.SELECT_COURSE,
                        ResponseCode.COURSE_FULL, "课程已满");
            }

            // 检查是否已选过该课程
            List<Course> selectedCourses = courseDao.getCoursesByStudentId(studentId);
            if (selectedCourses.stream().anyMatch(c -> c.getCourseId().equals(courseId))) {
                return createErrorResponse(Message.SELECT_COURSE,
                        ResponseCode.ALREADY_SELECTED, "已选过该课程");
            }

            String newCourseSchedule = course.getSchedule();
            if (newCourseSchedule != null && !newCourseSchedule.isEmpty()) {
                for (Course selectedCourse : selectedCourses) {
                    String selectedSchedule = selectedCourse.getSchedule();
                    if (selectedSchedule != null && !selectedSchedule.isEmpty()) {
                        if (hasTimeConflict(newCourseSchedule, selectedSchedule)) {
                            return createErrorResponse(Message.SELECT_COURSE,
                                    ResponseCode.TIME_CONFLICT,
                                    "时间冲突：与已选课程《" + selectedCourse.getCourseName() + "》时间重叠");
                        }
                    }
                }
            }

            // 执行选课
            int result = courseDao.selectCourse(studentId, courseId);
            if (result > 0) {
                Message response = new Message(Message.SELECT_COURSE);
                response.setStatus(ResponseCode.OK);
                response.setDescription("选课成功");
                return response;
            } else {
                return createErrorResponse(Message.SELECT_COURSE,
                        ResponseCode.FAIL, "选课失败");
            }
        } catch (Exception e) {
            return createErrorResponse(Message.SELECT_COURSE,
                    ResponseCode.INTERNAL_SERVER_ERROR, "选课异常: " + e.getMessage());
        }
    }

    // 辅助方法：检查时间冲突
    private boolean hasTimeConflict(String schedule1, String schedule2) {
        // 解析两个时间安排字符串
        TimeSlot slot1 = parseTimeSlot(schedule1);
        TimeSlot slot2 = parseTimeSlot(schedule2);

        if (slot1 == null || slot2 == null) {
            return false; // 解析失败，视为不冲突
        }

        // 星期不同，肯定不冲突
        if (!slot1.dayOfWeek.equals(slot2.dayOfWeek)) {
            return false;
        }

        // 检查节次是否重叠
        return !(slot1.endSection < slot2.startSection || slot2.endSection < slot1.startSection);
    }

    // 辅助方法：解析时间安排字符串
    private TimeSlot parseTimeSlot(String schedule) {
        if (schedule == null || schedule.trim().isEmpty()) {
            return null;
        }

        // 使用正则表达式匹配模式：星期 + 节次范围
        Pattern pattern = Pattern.compile("(周一|周二|周三|周四|周五|周六|周日)\\s*(\\d+)-(\\d+)节?");
        Matcher matcher = pattern.matcher(schedule);

        if (matcher.find()) {
            try {
                String dayOfWeek = matcher.group(1);
                int startSection = Integer.parseInt(matcher.group(2));
                int endSection = Integer.parseInt(matcher.group(3));

                return new TimeSlot(dayOfWeek, startSection, endSection);
            } catch (NumberFormatException e) {
                // 数字解析失败
                return null;
            }
        }

        return null; // 格式不匹配
    }

    // 内部类：表示时间槽（星期和节次范围）
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
            // 检查是否已选该课程
            List<Course> selectedCourses = courseDao.getCoursesByStudentId(studentId);
            if (selectedCourses.stream().noneMatch(c -> c.getCourseId().equals(courseId))) {
                return createErrorResponse(Message.DROP_COURSE,
                        ResponseCode.NOT_SELECTED, "未选该课程");
            }

            // 执行退课
            int result = courseDao.dropCourse(studentId, courseId);
            if (result > 0) {
                Message response = new Message(Message.DROP_COURSE);
                response.setStatus(ResponseCode.OK);
                response.setDescription("退课成功");
                return response;
            } else {
                return createErrorResponse(Message.DROP_COURSE,
                        ResponseCode.FAIL, "退课失败");
            }
        } catch (Exception e) {
            return createErrorResponse(Message.DROP_COURSE,
                    ResponseCode.INTERNAL_SERVER_ERROR, "退课异常: " + e.getMessage());
        }
    }

    @Override
    public Message dropCourseAD(String studentId, String courseId) {
        try {
            // 执行退课
            System.out.print(studentId+" "+courseId);
            int result = courseDao.dropCourse(studentId, courseId);
            if (result > 0) {
                Message response = new Message(Message.DROP_COURSE);
                response.setStatus(ResponseCode.OK);
                response.setDescription("退课成功");
                return response;
            } else {
                return createErrorResponse(Message.DROP_COURSE,
                        ResponseCode.FAIL, "退课失败");
            }
        } catch (Exception e) {
            return createErrorResponse(Message.DROP_COURSE,
                    ResponseCode.INTERNAL_SERVER_ERROR, "退课异常: " + e.getMessage());
        }
    }

    @Override
    public Message getSelectedCourses(String studentId) {
        try {
            List<Course> courses = courseDao.getCoursesByStudentId(studentId);
            Message response = new Message(Message.GET_SELECTED_COURSES);
            response.setStatus(ResponseCode.OK);
            response.addData("courses", courses);
            return response;
        } catch (Exception e) {
            return createErrorResponse(Message.GET_SELECTED_COURSES,
                    ResponseCode.INTERNAL_SERVER_ERROR, "获取已选课程失败: " + e.getMessage());
        }
    }

    @Override
    public Message getTeachingCourses(String teacherId) {
        try {
            List<Course> courses = courseDao.getCoursesByTeacherId(teacherId);
            Message response = new Message(Message.GET_TEACHING_COURSES);
            response.setStatus(ResponseCode.OK);
            response.addData("courses", courses);
            return response;
        } catch (Exception e) {
            return createErrorResponse(Message.GET_TEACHING_COURSES,
                    ResponseCode.INTERNAL_SERVER_ERROR, "获取授课课程失败: " + e.getMessage());
        }
    }

    @Override
    public Message addCourse(Course course) {
        try {
            // 验证课程信息
            if (course.getCourseId() == null || course.getCourseId().isEmpty()) {
                return createErrorResponse(Message.ADD_COURSE,
                        ResponseCode.BAD_REQUEST, "课程ID不能为空");
            }
            if (course.getCourseName() == null || course.getCourseName().isEmpty()) {
                return createErrorResponse(Message.ADD_COURSE,
                        ResponseCode.BAD_REQUEST, "课程名称不能为空");
            }
            if (course.getTeacherId() == null || course.getTeacherId().isEmpty()) {
                return createErrorResponse(Message.ADD_COURSE,
                        ResponseCode.BAD_REQUEST, "教师ID不能为空");
            }
            if (course.getTeacherName() == null || course.getTeacherName().isEmpty()) {
                return createErrorResponse(Message.ADD_COURSE,
                        ResponseCode.BAD_REQUEST, "教师姓名不能为空");
            }
            if (course.getSchedule() == null || course.getSchedule().isEmpty()) {
                return createErrorResponse(Message.ADD_COURSE,
                        ResponseCode.BAD_REQUEST, "时间安排不能为空");
            }
            if (course.getLocation() == null || course.getLocation().isEmpty()) {
                return createErrorResponse(Message.ADD_COURSE,
                        ResponseCode.BAD_REQUEST, "上课地点不能为空");
            }

            // 检查课程是否已存在
            if (courseDao.getCourseById(course.getCourseId()) != null) {
                return createErrorResponse(Message.ADD_COURSE,
                        ResponseCode.ALREADY_EXISTS, "课程ID已存在");
            }

            // 设置默认值
            if (course.getSelectedNum() == null || course.getSelectedNum() < 0) {
                course.setSelectedNum(0); // 新课程已选人数默认为0
            }
            if (course.getCapacity() == null || course.getCapacity() <= 0) {
                course.setCapacity(30); // 默认容量30人
            }
            if (course.getCredit() == null || course.getCredit() <= 0) {
                course.setCredit(2); // 默认学分2分
            }
            if (course.getStartWeek() == null || course.getStartWeek() <= 0) {
                course.setStartWeek(1); // 默认开始周为第1周
            }
            if (course.getEndWeek() == null || course.getEndWeek() <= 0) {
                course.setEndWeek(16); // 默认结束周为第16周
            }

            // 添加课程
            int result = courseDao.addCourse(course);
            if (result > 0) {
                Message response = new Message(Message.ADD_COURSE);
                response.setStatus(ResponseCode.OK);
                response.setDescription("课程添加成功");
                return response;
            } else {
                return createErrorResponse(Message.ADD_COURSE,
                        ResponseCode.FAIL, "课程添加失败");
            }
        } catch (Exception e) {
            return createErrorResponse(Message.ADD_COURSE,
                    ResponseCode.INTERNAL_SERVER_ERROR, "课程添加异常: " + e.getMessage());
        }
    }

    @Override
    public Message updateCourse(Course course) {
        try {
            // 验证课程信息
            if (course.getCourseId() == null || course.getCourseId().isEmpty()) {
                return createErrorResponse(Message.UPDATE_COURSE,
                        ResponseCode.BAD_REQUEST, "课程ID不能为空");
            }

            // 检查课程是否存在
            if (courseDao.getCourseById(course.getCourseId()) == null) {
                return createErrorResponse(Message.UPDATE_COURSE,
                        ResponseCode.NOT_FOUND, "课程不存在");
            }

            // 更新课程
            int result = courseDao.updateCourse(course);
            if (result > 0) {
                Message response = new Message(Message.UPDATE_COURSE);
                response.setStatus(ResponseCode.OK);
                response.setDescription("课程更新成功");
                return response;
            } else {
                return createErrorResponse(Message.UPDATE_COURSE,
                        ResponseCode.FAIL, "课程更新失败");
            }
        } catch (Exception e) {
            return createErrorResponse(Message.UPDATE_COURSE,
                    ResponseCode.INTERNAL_SERVER_ERROR, "课程更新异常: " + e.getMessage());
        }
    }

    @Override
    public Message deleteCourse(String courseId) {
        try {
            // 检查课程是否存在
            if (courseDao.getCourseById(courseId) == null) {
                return createErrorResponse(Message.DELETE_COURSE,
                        ResponseCode.NOT_FOUND, "课程不存在");
            }

            // 删除课程
            int result = courseDao.deleteCourse(courseId);
            if (result > 0) {
                Message response = new Message(Message.DELETE_COURSE);
                response.setStatus(ResponseCode.OK);
                response.setDescription("课程删除成功");
                return response;
            } else {
                return createErrorResponse(Message.DELETE_COURSE,
                        ResponseCode.FAIL, "课程删除失败");
            }
        } catch (Exception e) {
            return createErrorResponse(Message.DELETE_COURSE,
                    ResponseCode.INTERNAL_SERVER_ERROR, "课程删除异常: " + e.getMessage());
        }
    }

    @Override
    public Message getCourseSchedule(String studentId, String semester) {
        try {
            // 1. 获取学生已选课程
            List<Course> selectedCourses = courseDao.getCourseSchedule(studentId,semester);

            // 2. 构建课表数据结构
            CourseSchedule schedule = new CourseSchedule(studentId);
            schedule.setCourses(selectedCourses);

            // 3. 返回成功响应
            Message response = new Message(Message.GET_COURSE_SCHEDULE);
            response.setStatus(ResponseCode.OK);
            response.addData("schedule", schedule);
            return response;

        } catch (Exception e) {
            return createErrorResponse(Message.GET_COURSE_SCHEDULE,
                    ResponseCode.INTERNAL_SERVER_ERROR,
                    "获取课表失败: " + e.getMessage());
        }
    }
    @Override
    public Message getCourseById(String keyword) {
        try {
            // 通过课程ID精确查询课程
            Course course = courseDao.getCourseById(keyword);

            if (course != null) {
                // 找到匹配的课程
                Message response = new Message(Message.GET_COURSE_BY_ID);
                response.setStatus(ResponseCode.OK);
                response.addData("course", course);
                return response;
            } else {
                // 未找到课程
                return createErrorResponse(Message.GET_COURSE_BY_ID,
                        ResponseCode.NOT_FOUND, "未找到课程ID为 " + keyword + " 的课程");
            }
        } catch (Exception e) {
            return createErrorResponse(Message.GET_COURSE_BY_ID,
                    ResponseCode.INTERNAL_SERVER_ERROR, "查询课程异常: " + e.getMessage());
        }
    }

    @Override
    public Message getCourseByName(String keyword) {
        try {
            // 通过课程名称模糊查询课程
            List<Course> courses = courseDao.getCoursesByName(keyword);

            if (courses != null && !courses.isEmpty()) {
                // 找到匹配的课程
                Message response = new Message(Message.GET_COURSE_BY_NAME);
                response.setStatus(ResponseCode.OK);
                response.addData("courses", courses);
                return response;
            } else {
                // 未找到课程
                return createErrorResponse(Message.GET_COURSE_BY_NAME,
                        ResponseCode.NOT_FOUND, "未找到包含 '" + keyword + "' 的课程");
            }
        } catch (Exception e) {
            return createErrorResponse(Message.GET_COURSE_BY_NAME,
                    ResponseCode.INTERNAL_SERVER_ERROR, "查询课程异常: " + e.getMessage());
        }
    }

    @Override
    public Message getSelectionRecords(String courseId) {
        try {
            // 1. 检查课程是否存在
            Course course = courseDao.getCourseById(courseId);
            if (course == null) {
                return createErrorResponse(Message.GET_SELECTION_RECORDS,
                        ResponseCode.NOT_FOUND, "课程不存在");
            }

            // 2. 获取该课程的选课记录
            List<SelectionRecord> records = courseDao.getSelectionRecords(courseId);

            // 3. 返回成功响应
            Message response = new Message(Message.GET_SELECTION_RECORDS);
            response.setStatus(ResponseCode.OK);
            response.addData("records", records);
            return response;

        } catch (Exception e) {
            return createErrorResponse(Message.GET_SELECTION_RECORDS,
                    ResponseCode.INTERNAL_SERVER_ERROR, "获取选课记录失败: " + e.getMessage());
        }
    }
    // 辅助方法：创建错误响应
    private Message createErrorResponse(String type, int status, String description) {
        Message response = new Message(type);
        response.setStatus(status);
        response.setDescription(description);
        return response;
    }
}