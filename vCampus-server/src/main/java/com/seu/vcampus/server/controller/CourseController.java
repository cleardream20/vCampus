package com.seu.vcampus.server.controller;

import com.google.gson.JsonObject;
import com.seu.vcampus.common.model.course.Course;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Jsonable;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.server.service.CourseService;
import com.seu.vcampus.server.service.CourseServiceImpl;

import java.util.Map;

public class CourseController implements RequestController {
    private final CourseService courseService = new CourseServiceImpl();

    @Override
    public Message handleRequest(Message request) {
        Object rawData = request.getData();
        Map<String, Object> dataMap = null;
        User user = null;

        if (rawData instanceof Map) {
            dataMap = (Map<String, Object>) rawData;
            user = extractUserFromData(dataMap);
        } else if (rawData instanceof JsonObject) {
            JsonObject data = (JsonObject) rawData;
            user = extractUserFromJsonObject(data);
        }

        switch (request.getType()) {
            case Message.GET_COURSE_LIST:
                return courseService.getCourseList();

            case Message.GET_COURSE_BY_ID:
                if (dataMap != null) {
                    String keywordById = (String) dataMap.get("keyword");
                    return courseService.getCourseById(keywordById);
                }
                return createErrorResponse(request, "缺少查询参数");

            case Message.GET_COURSE_BY_NAME:
                if (dataMap != null) {
                    String keywordByName = (String) dataMap.get("keyword");
                    return courseService.getCourseByName(keywordByName);
                }
                return createErrorResponse(request, "缺少查询参数");

            case Message.DROP_COURSE:
            case Message.SELECT_COURSE:
            case Message.GET_SELECTED_COURSES:
            case Message.GET_COURSE_SCHEDULE:
                return handleStudentCourseOperations(request, user, dataMap);

            case Message.ADD_COURSE:
            case Message.UPDATE_COURSE:
            case Message.DELETE_COURSE:
            case Message.GET_SELECTION_RECORDS:
            case Message.DROP_COURSE_AD:
                return handleAdminCourseOperations(request, user, dataMap);

            default:
                return createErrorResponse(request, "未知请求类型");
        }
    }

    private User extractUserFromData(Map<String, Object> dataMap) {
        if (dataMap == null || !dataMap.containsKey("user")) {
            return null;
        }
        Object userObj = dataMap.get("user");
        if (userObj instanceof User) {
            return (User) userObj;
        }
        // 如果是JsonElement或其他类型，转换为JSON字符串再解析
        String userJson = Jsonable.toJson(userObj);
        return Jsonable.fromJson(userJson, User.class);
    }

    private User extractUserFromJsonObject(JsonObject data) {
        if (data == null || !data.has("user")) {
            return null;
        }
        try {
            // 尝试直接获取User对象
            return Jsonable.gson.fromJson(data.get("user"), User.class);
        } catch (Exception e) {
            // 如果失败，尝试作为字符串解析
            String userData = data.get("user").getAsString();
            return Jsonable.fromJson(userData, User.class);
        }
    }

    // 处理学生课程操作
    private Message handleStudentCourseOperations(Message request, User user, Map<String, Object> dataMap) {
        if (user == null) {
            return createErrorResponse(request, "用户未登录，请先登录");
        }

        if (!"ST".equals(user.getRole())) {
            return createErrorResponse(request, "无权限执行此操作");
        }

//        String studentId = user.getTsid();
        String studentId = user.getCid();

        switch (request.getType()) {
            case Message.SELECT_COURSE:
                String courseId = (String) dataMap.get("courseId");
                return courseService.selectCourse(studentId, courseId);

            case Message.DROP_COURSE:
                String dropCourseId = (String) dataMap.get("courseId");
                return courseService.dropCourse(studentId, dropCourseId);

            case Message.GET_SELECTED_COURSES:
                return courseService.getSelectedCourses(studentId);

            case Message.GET_COURSE_SCHEDULE:
                String semester = (String) dataMap.get("semester");
                return courseService.getCourseSchedule(studentId, semester);

            default:
                return createErrorResponse(request, "请求处理失败");
        }
    }

    // 处理管理员课程操作
    private Message handleAdminCourseOperations(Message request, User user, Map<String, Object> dataMap) {
        if (user == null) {
            return createErrorResponse(request, "用户未登录，请先登录");
        }

        if (!"AD".equals(user.getRole())) {
            return createErrorResponse(request, "无权限执行此操作");
        }

        switch (request.getType()) {
            case Message.ADD_COURSE:
                String courseJson = Jsonable.toJson(dataMap.get("course"));
                Course newCourse = Jsonable.fromJson(courseJson, Course.class);
                return courseService.addCourse(newCourse);

            case Message.UPDATE_COURSE:
                String updatedCourseJson = Jsonable.toJson(dataMap.get("course"));
                Course updatedCourse = Jsonable.fromJson(updatedCourseJson, Course.class);
                return courseService.updateCourse(updatedCourse);

            case Message.DELETE_COURSE:
                String courseId = (String) dataMap.get("courseId");
                return courseService.deleteCourse(courseId);

            case Message.GET_SELECTION_RECORDS:
                String courseId_1 = (String) dataMap.get("courseId");
                return courseService.getSelectionRecords(courseId_1);

            case Message.DROP_COURSE_AD:
                String courseId_2 = (String) dataMap.get("courseId");
                String studentId_2 = (String) dataMap.get("studentId");
                return courseService.dropCourseAD(studentId_2, courseId_2);

            default:
                return createErrorResponse(request, "请求处理失败");
        }
    }

    private Message createErrorResponse(Message request, String message) {
        Message response = new Message(request.getType());
        response.setStatus(Message.STATUS_ERROR);
        response.setMessage(message);
        return response;
    }
}