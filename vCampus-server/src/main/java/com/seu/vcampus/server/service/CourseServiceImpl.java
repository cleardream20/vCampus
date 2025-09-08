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

public class CourseServiceImpl implements CourseService {
    private final CourseDao courseDao ;

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
            Message response = new Message(Message.GET_COURSE_LIST);
            response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR);
            response.setDescription("获取课程列表失败: " + e.getMessage());
            return response;
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

            // 检查课程是否已满
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

            // 检查课程是否已存在
            if (courseDao.getCourseById(course.getCourseId()) != null) {
                return createErrorResponse(Message.ADD_COURSE,
                        ResponseCode.ALREADY_EXISTS, "课程ID已存在");
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
    public Message getCourseSchedule(String studentId) {
        try {
            // 1. 获取学生已选课程
            List<Course> selectedCourses = courseDao.getCoursesByStudentId(studentId);

            // 2. 构建课表数据结构（示例）
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
    // 辅助方法：创建错误响应
    private Message createErrorResponse(String type, int status, String description) {
        Message response = new Message(type);
        response.setStatus(status);
        response.setDescription(description);
        return response;
    }
}