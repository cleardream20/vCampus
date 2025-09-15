package com.seu.vcampus.client.controller;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.seu.vcampus.client.socket.ClientSocketUtil;
import com.seu.vcampus.common.model.course.Course;
import com.seu.vcampus.common.model.course.CourseSchedule;
import com.seu.vcampus.common.model.course.SelectionRecord;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Jsonable;
import com.seu.vcampus.common.util.Message;

import java.io.IOException;
import java.util.List;

public class CourseController {

    public CourseController() {
    }

    public List<Course> getCourseList() {
        Message request = new Message(Message.GET_COURSE_LIST);

        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (Message.STATUS_SUCCESS.equals(response.getStatus())) {
                List<Course> courses = Jsonable.fromJson(
                        Jsonable.toJson(response.getData()),
                        new TypeToken<List<Course>>(){}.getType()
                );
                return courses;
            }
        } catch (IOException e) {
            System.err.println("获取课程列表出错: " + e.getMessage());
        }
        return null;
    }

    public boolean addCourse(Course course, User user) {
        Message request = new Message(Message.ADD_COURSE);
        JsonObject data = new JsonObject();
        data.addProperty("course", Jsonable.toJson(course));
        data.addProperty("user", Jsonable.toJson(user));
        request.setData(data);

        try {
            Message response = ClientSocketUtil.sendRequest(request);
            return Message.STATUS_SUCCESS.equals(response.getStatus());
        } catch (IOException e) {
            System.err.println("添加课程出错: " + e.getMessage());
            return false;
        }
    }

    public boolean updateCourse(Course course, User user) {
        Message request = new Message(Message.UPDATE_COURSE);
        JsonObject data = new JsonObject();
        data.addProperty("course", Jsonable.toJson(course));
        data.addProperty("user", Jsonable.toJson(user));
        request.setData(data);

        try {
            Message response = ClientSocketUtil.sendRequest(request);
            return Message.STATUS_SUCCESS.equals(response.getStatus());
        } catch (IOException e) {
            System.err.println("更新课程出错: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCourse(String courseId, User user) {
        Message request = new Message(Message.DELETE_COURSE);
        JsonObject data = new JsonObject();
        data.addProperty("courseId", courseId);
        data.addProperty("user", Jsonable.toJson(user));
        request.setData(data);

        try {
            Message response = ClientSocketUtil.sendRequest(request);
            return Message.STATUS_SUCCESS.equals(response.getStatus());
        } catch (IOException e) {
            System.err.println("删除课程出错: " + e.getMessage());
            return false;
        }
    }

    public Course getCourseById(String keyword) {
        Message request = new Message(Message.GET_COURSE_BY_ID);
        JsonObject data = new JsonObject();
        data.addProperty("keyword", keyword);
        request.setData(data);

        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (Message.STATUS_SUCCESS.equals(response.getStatus())) {
                JsonObject responseData = (JsonObject) response.getData();
                String courseJson = responseData.get("course").getAsString();
                return Jsonable.fromJson(courseJson, Course.class);
            }
        } catch (IOException e) {
            System.err.println("根据ID获取课程出错: " + e.getMessage());
        }
        return null;
    }

    public List<Course> getCourseByName(String keyword) {
        Message request = new Message(Message.GET_COURSE_BY_NAME);
        JsonObject data = new JsonObject();
        data.addProperty("keyword", keyword);
        request.setData(data);

        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (Message.STATUS_SUCCESS.equals(response.getStatus())) {
                JsonObject responseData = (JsonObject) response.getData();
                String coursesJson = responseData.get("courses").getAsString();
                return Jsonable.fromJson(coursesJson, new TypeToken<List<Course>>(){}.getType());
            }
        } catch (IOException e) {
            System.err.println("根据名称获取课程出错: " + e.getMessage());
        }
        return null;
    }

    public Message selectCourse(String studentId, String courseId, User user) {
        Message request = new Message(Message.SELECT_COURSE);
        JsonObject data = new JsonObject();
        data.addProperty("studentId", studentId);
        data.addProperty("courseId", courseId);
        data.addProperty("user", Jsonable.toJson(user));
        request.setData(data);

        try {
            return ClientSocketUtil.sendRequest(request);
        } catch (IOException e) {
            System.err.println("选课出错: " + e.getMessage());
            return Message.error(Message.SELECT_COURSE, "网络错误: " + e.getMessage());
        }
    }

    public List<Course> getCoursesByStudentId(String studentId, User user) {
        Message request = new Message(Message.GET_SELECTED_COURSES);
        JsonObject data = new JsonObject();
        data.addProperty("studentId", studentId);
        data.addProperty("user", Jsonable.toJson(user));
        request.setData(data);

        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (Message.STATUS_SUCCESS.equals(response.getStatus())) {
                JsonObject responseData = (JsonObject) response.getData();
                String coursesJson = responseData.get("courses").getAsString();
                return Jsonable.fromJson(coursesJson, new TypeToken<List<Course>>(){}.getType());
            }
        } catch (IOException e) {
            System.err.println("获取学生已选课程出错: " + e.getMessage());
        }
        return null;
    }

    public boolean dropCourse(String studentId, String courseId, User user) {
        Message request = new Message(Message.DROP_COURSE);
        JsonObject data = new JsonObject();
        data.addProperty("studentId", studentId);
        data.addProperty("courseId", courseId);
        data.addProperty("user", Jsonable.toJson(user));
        request.setData(data);

        try {
            Message response = ClientSocketUtil.sendRequest(request);
            return Message.STATUS_SUCCESS.equals(response.getStatus());
        } catch (IOException e) {
            System.err.println("退课出错: " + e.getMessage());
            return false;
        }
    }

    public CourseSchedule getCoursesSchedule(String id, User user, String semester) {
        Message request = new Message(Message.GET_COURSE_SCHEDULE);
        JsonObject data = new JsonObject();
        data.addProperty("studentId", id);
        data.addProperty("user", Jsonable.toJson(user));
        data.addProperty("semester", semester);
        request.setData(data);

        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (Message.STATUS_SUCCESS.equals(response.getStatus())) {
                JsonObject responseData = (JsonObject) response.getData();
                String scheduleJson = responseData.get("schedule").getAsString();
                return Jsonable.fromJson(scheduleJson, CourseSchedule.class);
            }
        } catch (IOException e) {
            System.err.println("获取课程表出错: " + e.getMessage());
        }
        return null;
    }

    public List<SelectionRecord> getSelectionRecords(String courseId, User user) {
        Message request = new Message(Message.GET_SELECTION_RECORDS);
        JsonObject data = new JsonObject();
        data.addProperty("courseId", courseId);
        data.addProperty("user", Jsonable.toJson(user));
        request.setData(data);

        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (Message.STATUS_SUCCESS.equals(response.getStatus())) {
                JsonObject responseData = (JsonObject) response.getData();
                String recordsJson = responseData.get("records").getAsString();
                return Jsonable.fromJson(recordsJson, new TypeToken<List<SelectionRecord>>(){}.getType());
            }
        } catch (IOException e) {
            System.err.println("获取选课记录出错: " + e.getMessage());
        }
        return null;
    }

    public boolean dropCourseAD(String studentId, String courseId, User currentUser) {
        Message request = new Message(Message.DROP_COURSE_AD);
        JsonObject data = new JsonObject();
        data.addProperty("studentId", studentId);
        data.addProperty("courseId", courseId);
        data.addProperty("user", Jsonable.toJson(currentUser));
        request.setData(data);

        try {
            Message response = ClientSocketUtil.sendRequest(request);
            return Message.STATUS_SUCCESS.equals(response.getStatus());
        } catch (IOException e) {
            System.err.println("管理员退课出错: " + e.getMessage());
            return false;
        }
    }
}