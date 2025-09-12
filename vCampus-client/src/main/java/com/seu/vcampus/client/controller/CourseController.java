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

    public boolean updateCourse(Course course,User user) {
        Message request = new Message(Message.UPDATE_COURSE);
        request.addData("course", course);
        request.addData("user", user);
        Message response = socketHandler.sendMessage(request);
        return response.getStatus() == ResponseCode.OK;
    }

    public boolean deleteCourse(String courseId,User user) {
        Message request = new Message(Message.DELETE_COURSE);
        request.addData("courseId", courseId);
        request.addData("user", user);
        Message response = socketHandler.sendMessage(request);
        return response.getStatus() == ResponseCode.OK;
    }

    public Course getCourseById(String keyword) {
        Message request = new Message(Message.GET_COURSE_BY_ID);
        request.addData("keyword", keyword);
        Message response = socketHandler.sendMessage(request);
        if (response.getStatus() == ResponseCode.OK) {
            return (Course) response.getData().get("course");
        }
        return null;
    }

    public List<Course> getCourseByName(String keyword) {
        Message request = new Message(Message.GET_COURSE_BY_NAME);
        request.addData("keyword", keyword);
        Message response = socketHandler.sendMessage(request);

        if (response.getStatus() == ResponseCode.OK) {
            return (List<Course>) response.getData().get("courses");
        }
        return null;
    }

    public Message selectCourse(String studentId, String courseId,User user) {
        Message request = new Message(Message.SELECT_COURSE);
        request.addData("studentId",studentId);
        request.addData("courseId", courseId);
        request.addData("user", user);
        Message response = socketHandler.sendMessage(request);

        return response;
    }

    public List<Course> getCoursesByStudentId(String studentId,User user) {
        Message request = new Message(Message.GET_SELECTED_COURSES);
        request.addData("studentId", studentId);
        request.addData("user", user);
        Message response = socketHandler.sendMessage(request);

        if (response.getStatus() == ResponseCode.OK) {
            return (List<Course>) response.getData().get("courses");
        }
        return null;
    }

    // 实现退课方法
    public boolean dropCourse(String studentId, String courseId,User user) {
        Message request = new Message(Message.DROP_COURSE);
        request.addData("studentId", studentId);
        request.addData("courseId", courseId);
        request.addData("user", user);
        Message response = socketHandler.sendMessage(request);
        return response.getStatus() == ResponseCode.OK;
    }
}