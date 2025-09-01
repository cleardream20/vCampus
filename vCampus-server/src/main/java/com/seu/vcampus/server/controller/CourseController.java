package com.seu.vcampus.server.controller;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.ResponseCode;
import com.seu.vcampus.server.DataManager;

import java.util.List;

public class CourseController {

    public List<Course> getAllCourses() {
        return DataManager.getAllCourses();
    }

    public List<Course> getSelectedCourses(String studentId) {
        return DataManager.getSelectedCourses(studentId);
    }

    public boolean selectCourse(String studentId, String courseId) {
        return DataManager.selectCourse(studentId, courseId);
    }

    public boolean dropCourse(String studentId, String courseId) {
        return DataManager.dropCourse(studentId, courseId);
    }

    public Message processRequest(Message request) {
        Message response = new Message(request.getType());
        response.setSender("SERVER");

        try {
            String studentId = request.getSender();

            switch (request.getType()) {
                case "QUERY_ALL_COURSES":
                    List<Course> allCourses = getAllCourses();
                    response.addData("courses", allCourses);
                    response.setStatus(ResponseCode.OK);
                    break;

                case "QUERY_SELECTED_COURSES":
                    List<Course> selectedCourses = getSelectedCourses(studentId);
                    response.addData("selectedCourses", selectedCourses);
                    response.setStatus(ResponseCode.OK);
                    break;

                case "SELECT_COURSE":
                    String courseId = (String) request.getData("courseId");
                    boolean selectSuccess = selectCourse(studentId, courseId);
                    response.setStatus(selectSuccess ? ResponseCode.OK : ResponseCode.BAD_REQUEST);
                    response.setDescription(selectSuccess ? "选课成功" : "选课失败");
                    break;

                case "DROP_COURSE":
                    courseId = (String) request.getData("courseId");
                    boolean dropSuccess = dropCourse(studentId, courseId);
                    response.setStatus(dropSuccess ? ResponseCode.OK : ResponseCode.BAD_REQUEST);
                    response.setDescription(dropSuccess ? "退课成功" : "退课失败");
                    break;

                default:
                    response.setStatus(ResponseCode.NOT_FOUND);
                    response.setDescription("未知请求类型");
                    break;
            }
        } catch (Exception e) {
            response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR);
            response.setDescription("服务器错误: " + e.getMessage());
        }

        return response;
    }
}