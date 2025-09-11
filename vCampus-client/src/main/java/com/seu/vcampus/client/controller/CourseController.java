package com.seu.vcampus.client.controller;

import com.seu.vcampus.client.socket.ClientSocketHandler;
import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.ResponseCode;

import java.util.List;

public class CourseController {
    private final ClientSocketHandler socketHandler;

    public CourseController() {
        // 初始化Socket连接
        this.socketHandler = new ClientSocketHandler("localhost", 8888);
    }

    public List<Course> getCourseList() {
        Message request = new Message(Message.GET_COURSE_LIST);
        Message response = socketHandler.sendMessage(request);

        if (response.getStatus() == ResponseCode.OK) {
            return (List<Course>) response.getData().get("courses");
        }
        return null;
    }

    public boolean addCourse(Course course,User user) {
        Message request = new Message(Message.ADD_COURSE);
        request.addData("course", course);
        request.addData("user", user);
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
}