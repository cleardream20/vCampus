package com.seu.vcampus.client.controller;

import com.seu.vcampus.client.socket.ClientSocketHandler;
import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.ResponseCode;

import java.util.Collections;
import java.util.List;

public class CourseController {
    private final ClientSocketHandler socketHandler;
    private String currentUserId;

    public CourseController() {
        this.socketHandler = ClientSocketHandler.getInstance();
    }

    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }

    public List<Course> getAllCourses() {
        try {
            Message request = createRequest("QUERY_ALL_COURSES");
            socketHandler.sendMessage(request);

            Message response = socketHandler.getResponse(5000); // 5秒超时

            return processCourseResponse(response);
        } catch (Exception e) {
            System.err.println("获取课程列表失败: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Course> getSelectedCourses() {
        try {
            Message request = createRequest("QUERY_SELECTED_COURSES");
            socketHandler.sendMessage(request);

            Message response = socketHandler.getResponse(5000); // 5秒超时

            return processCourseResponse(response);
        } catch (Exception e) {
            System.err.println("获取已选课程失败: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Course> processCourseResponse(Message response) {
        if (response == null) {
            System.err.println("获取响应超时");
            return Collections.emptyList();
        }

        if (response.getStatus() == ResponseCode.OK) {
            return (List<Course>) response.getData("courses");
        } else {
            System.err.println("服务器错误: " + response.getDescription());
            return Collections.emptyList();
        }
    }

    public boolean selectCourse(String courseId) {
        try {
            Message request = createRequest("SELECT_COURSE");
            request.addData("courseId", courseId);
            socketHandler.sendMessage(request);

            Message response = socketHandler.getResponse(3000); // 3秒超时

            return processOperationResponse(response);
        } catch (Exception e) {
            System.err.println("选课请求失败: " + e.getMessage());
            return false;
        }
    }

    public boolean dropCourse(String courseId) {
        try {
            Message request = createRequest("DROP_COURSE");
            request.addData("courseId", courseId);
            socketHandler.sendMessage(request);

            Message response = socketHandler.getResponse(3000); // 3秒超时

            return processOperationResponse(response);
        } catch (Exception e) {
            System.err.println("退课请求失败: " + e.getMessage());
            return false;
        }
    }

    private boolean processOperationResponse(Message response) {
        if (response == null) {
            System.err.println("操作响应超时");
            return false;
        }

        return response.getStatus() == ResponseCode.OK;
    }

    private Message createRequest(String type) {
        Message request = new Message(type);
        request.setSender(currentUserId);
        return request;
    }
}