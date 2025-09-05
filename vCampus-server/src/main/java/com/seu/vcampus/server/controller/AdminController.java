package com.seu.vcampus.server.controller;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.CourseSelectionRule;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.ResponseCode;
import com.seu.vcampus.server.service.AdminService;
import com.seu.vcampus.server.service.AdminServiceImpl;

public class AdminController {
    private AdminService adminService = new AdminServiceImpl();

    public Message handleAdminRequest(Message request) {
        switch (request.getType()) {
            case Message.ADD_COURSE:
                Course course = (Course) request.getData().get("course");
                return adminService.addCourse(course);
            case Message.UPDATE_COURSE:
                course = (Course) request.getData().get("course");
                return adminService.updateCourse(course);
            case Message.DELETE_COURSE:
                String courseId = (String) request.getData().get("courseId");
                return adminService.deleteCourse(courseId);
            case Message.CONFIGURE_RULE:
                CourseSelectionRule rule = (CourseSelectionRule) request.getData().get("rule");
                return adminService.configureRule(rule);
            case Message.GET_RULE:
                return adminService.getRule();
            case Message.GENERATE_REPORT:
                return adminService.generateReport();
            default:
                Message response = new Message(request.getType());
                response.setStatus(ResponseCode.BAD_REQUEST);
                response.setDescription("未知请求类型");
                return response;
        }
    }
}
