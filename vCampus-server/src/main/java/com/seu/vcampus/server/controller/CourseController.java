package com.seu.vcampus.server.controller;

import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.ResponseCode;
import com.seu.vcampus.server.service.CourseService;
import com.seu.vcampus.server.service.CourseServiceImpl;

public class CourseController {
    private CourseService courseService = new CourseServiceImpl();

    public Message handleCourseRequest(Message request, User user) {
        String studentId = user.getId();

        switch (request.getType()) {
            case Message.GET_COURSE_LIST:
                return courseService.getCourseList();
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
            default:
                Message response = new Message(request.getType());
                response.setStatus(ResponseCode.BAD_REQUEST);
                response.setDescription("未知请求类型");
                return response;
        }
    }
}