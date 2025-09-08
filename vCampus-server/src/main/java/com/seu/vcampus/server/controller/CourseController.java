package com.seu.vcampus.server.controller;

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
                // 获取课程列表不需要用户信息
                return courseService.getCourseList();

            case Message.SELECT_COURSE:
            case Message.DROP_COURSE:
            case Message.GET_SELECTED_COURSES:
            case Message.GET_COURSE_SCHEDULE:
                // 这些操作需要用户信息
                if (user == null) {
                    return createErrorResponse(request, ResponseCode.UNAUTHORIZED, "用户未登录，请先登录");
                }
                String studentId = user.getId();

                // 根据具体请求类型处理
                switch (request.getType()) {
                    case Message.SELECT_COURSE:
                        String courseId = (String) request.getData().get("courseId");
                        return courseService.selectCourse(studentId, courseId);
                    case Message.DROP_COURSE:
                        courseId = (String) request.getData().get("courseId");
                        return courseService.dropCourse(studentId, courseId);
                    case Message.GET_SELECTED_COURSES:
                        return courseService.getSelectedCourses(studentId);
                    case Message.GET_COURSE_SCHEDULE:
                        return courseService.getCourseSchedule(studentId);
                }
                break;

            default:
                return createErrorResponse(request, ResponseCode.BAD_REQUEST, "未知请求类型");
        }

        // 如果执行到这里，返回内部错误
        return createErrorResponse(request, ResponseCode.INTERNAL_SERVER_ERROR, "请求处理失败");
    }

    // 创建错误响应的辅助方法
    private Message createErrorResponse(Message request, int code, String description) {
        Message response = new Message(request.getType());
        response.setStatus(code);
        response.setDescription(description);
        return response;
    }
}