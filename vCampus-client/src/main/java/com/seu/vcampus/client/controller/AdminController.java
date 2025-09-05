package com.seu.vcampus.client.controller;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.CourseSelectionRule;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.client.socket.ClientSocketHandler;
import com.seu.vcampus.common.util.ResponseCode;

public class AdminController {
    private ClientSocketHandler socketHandler;

    public AdminController(ClientSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    public boolean addCourse(Course course) {
        Message request = new Message(Message.ADD_COURSE);
        request.addData("course", course);
        Message response = socketHandler.sendMessage(request);
        return response.getStatus() == ResponseCode.OK;
    }

    public boolean updateCourse(Course course) {
        Message request = new Message(Message.UPDATE_COURSE);
        request.addData("course", course);
        Message response = socketHandler.sendMessage(request);
        return response.getStatus() == ResponseCode.OK;
    }

    public boolean deleteCourse(String courseId) {
        Message request = new Message(Message.DELETE_COURSE);
        request.addData("courseId", courseId);
        Message response = socketHandler.sendMessage(request);
        return response.getStatus() == ResponseCode.OK;
    }

    public boolean configureRule(CourseSelectionRule rule) {
        Message request = new Message(Message.CONFIGURE_RULE);
        request.addData("rule", rule);
        Message response = socketHandler.sendMessage(request);
        return response.getStatus() == ResponseCode.OK;
    }

    public CourseSelectionRule getRule() {
        Message request = new Message(Message.GET_RULE);
        Message response = socketHandler.sendMessage(request);
        if (response.getStatus() == ResponseCode.OK) {
            return (CourseSelectionRule) response.getData().get("rule");
        }
        return null;
    }

    public String generateReport() {
        Message request = new Message(Message.GENERATE_REPORT);
        Message response = socketHandler.sendMessage(request);

        if (response.getStatus() == ResponseCode.OK) {
            return (String) response.getData().get("report");
        } else {
            return "生成报表失败: " + response.getDescription();
        }
    }
}