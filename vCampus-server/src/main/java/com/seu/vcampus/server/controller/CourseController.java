package com.seu.vcampus.server.controller;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.ResponseCode;
import com.seu.vcampus.server.service.CourseService;
import com.seu.vcampus.server.service.CourseServiceImpl;

public class CourseController {
    private CourseService courseService = new CourseServiceImpl();

    public Message handleCourseRequest(Message request, User user) {
        switch (request.getType()) {
            case Message.GET_COURSE_LIST:
                return courseService.getCourseList();
            case Message.GET_COURSE_BY_ID:
                return courseService.getCourseById((String) request.getData().get("keyword"));
            case Message.GET_COURSE_BY_NAME:
                return courseService.getCourseByName((String) request.getData().get("keyword"));
            case Message.DROP_COURSE:
            case Message.SELECT_COURSE:
            case Message.GET_SELECTED_COURSES:
            case Message.GET_COURSE_SCHEDULE:
                // 学生选课相关操作
                return handleStudentCourseOperations(request, user);

            case Message.ADD_COURSE:
            case Message.UPDATE_COURSE:
            case Message.DELETE_COURSE:
            case Message.GET_SELECTION_RECORDS:
            case Message.DROP_COURSE_AD:
                // 管理员课程管理操作
                return handleAdminCourseOperations(request, user);

            default:
                return createErrorResponse(request, ResponseCode.BAD_REQUEST, "未知请求类型");
        }
    }

    // 处理学生课程操作
    private Message handleStudentCourseOperations(Message request, User user) {
        // 验证用户是否登录
        if (user == null) {
            return createErrorResponse(request, ResponseCode.UNAUTHORIZED, "用户未登录，请先登录");
        }

        // 验证用户角色是否为学生
        if (!"ST".equals(user.getRole())) {
            return createErrorResponse(request, ResponseCode.FORBIDDEN, "无权限执行此操作");
        }

        String studentId = user.getId();
        String courseId = (String) request.getData().get("courseId");
        String semester= (String) request.getData().get("semester");
        switch (request.getType()) {
            case Message.SELECT_COURSE:
                return courseService.selectCourse(studentId, courseId);
            case Message.DROP_COURSE:
                return courseService.dropCourse(studentId, courseId);
            case Message.GET_SELECTED_COURSES:
                return courseService.getSelectedCourses(studentId);
            case Message.GET_COURSE_SCHEDULE:
                return courseService.getCourseSchedule(studentId,semester);
            default:
                return createErrorResponse(request, ResponseCode.INTERNAL_SERVER_ERROR, "请求处理失败");
        }
    }

    // 处理管理员课程操作
    private Message handleAdminCourseOperations(Message request, User user) {
        // 验证用户是否登录
        if (user == null) {
            return createErrorResponse(request, ResponseCode.UNAUTHORIZED, "用户未登录，请先登录");
        }

        // 验证用户是否为管理员
        if (!"AD".equals(user.getRole())) {
            return createErrorResponse(request, ResponseCode.FORBIDDEN, "无权限执行此操作");
        }

        switch (request.getType()) {
            case Message.ADD_COURSE:
                Course newCourse = (Course) request.getData().get("course");
                return courseService.addCourse(newCourse);

            case Message.UPDATE_COURSE:
                Course updatedCourse = (Course) request.getData().get("course");
                return courseService.updateCourse(updatedCourse);

            case Message.DELETE_COURSE:
                String courseId = (String) request.getData().get("courseId");
                return courseService.deleteCourse(courseId);

            case Message.GET_SELECTION_RECORDS:
                String courseId_1 = (String) request.getData().get("courseId");
                return courseService.getSelectionRecords(courseId_1);
            case Message.DROP_COURSE_AD:
                String courseId_2 = (String) request.getData().get("courseId");
                String studentId_2 = (String) request.getData().get("studentId");
                return courseService.dropCourseAD(studentId_2, courseId_2);
            default:
                return createErrorResponse(request, ResponseCode.INTERNAL_SERVER_ERROR, "请求处理失败");
        }
    }

    // 创建错误响应的辅助方法
    private Message createErrorResponse(Message request, int code, String description) {
        Message response = new Message(request.getType());
        response.setStatus(code);
        response.setDescription(description);
        return response;
    }
}