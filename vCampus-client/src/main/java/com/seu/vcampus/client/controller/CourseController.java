package com.seu.vcampus.client.controller;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.client.socket.ClientSocketHandler;
import com.seu.vcampus.common.util.ResponseCode;

import java.util.List;

public class CourseController {
    private ClientSocketHandler socketHandler;

    public CourseController(ClientSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    public List<Course> getCourseList() {
        Message request = new Message(Message.GET_COURSE_LIST);
        Message response = socketHandler.sendMessage(request);
        if (response.getStatus() == ResponseCode.OK) {
            return (List<Course>) response.getData().get("courses");
        }
        return null;
    }

    public boolean selectCourse(String studentId, String courseId) {
        Message request = new Message(Message.SELECT_COURSE);
        request.addData("studentId", studentId);
        request.addData("courseId", courseId);
        Message response = socketHandler.sendMessage(request);
        return response.getStatus() == ResponseCode.OK;
    }

    public boolean dropCourse(String studentId, String courseId) {
        Message request = new Message(Message.DROP_COURSE);
        request.addData("studentId", studentId);
        request.addData("courseId", courseId);
        Message response = socketHandler.sendMessage(request);
        return response.getStatus() == ResponseCode.OK;
    }

    public List<Course> getSelectedCourses(String studentId) {
        Message request = new Message(Message.GET_SELECTED_COURSES);
        request.addData("studentId", studentId);
        Message response = socketHandler.sendMessage(request);
        if (response.getStatus() == ResponseCode.OK) {
            return (List<Course>) response.getData().get("courses");
        }
        return null;
    }

    public List<Course> getCourseSchedule(String studentId) {
        Message request = new Message(Message.GET_COURSE_SCHEDULE);
        request.addData("studentId", studentId);
        Message response = socketHandler.sendMessage(request);
        if (response.getStatus() == ResponseCode.OK) {
            return (List<Course>) response.getData().get("courses");
        }
        return null;
    }

    public List<Course> getTeachingCourses(String teacherId) {
        Message request = new Message(Message.GET_TEACHING_COURSES);
        request.addData("teacherId", teacherId);
        Message response = socketHandler.sendMessage(request);
        if (response.getStatus() == ResponseCode.OK) {
            return (List<Course>) response.getData().get("courses");
        }
        return null;
    }
}