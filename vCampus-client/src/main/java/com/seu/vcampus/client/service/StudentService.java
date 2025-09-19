package com.seu.vcampus.client.service;

import com.google.gson.reflect.TypeToken;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Jsonable;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.client.socket.ClientSocketUtil;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentService {

    public Student getStudent(String cid) throws Exception {
        if(cid == null || cid.trim().isEmpty()) {
            throw new IllegalArgumentException("cid is null");
        }

        Message request = new Message(Message.ST_STUDENT, cid);
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if(response == null) {
                throw new Exception("服务器无响应");
            }

            if(!response.isSuccess()) {
                throw new Exception(response.getMessage() != null ? response.getMessage() : "查询失败");
            }

            Student student = Jsonable.fromJson(Jsonable.toJson(response.getData()), Student.class);
            if(student == null) {
                throw new Exception("信息解析失败");
            }

            return student;
        } catch (IOException e) {
            throw new Exception("无法连接至服务器");
        }
    }

    public List<Student> getDataWithFilters(HashMap<Integer, String> filters) throws Exception {

        Message request = new Message(Message.AD_STUDENT, filters);
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if(response == null) {
                throw new Exception("服务器无响应");
            }

            if(!response.isSuccess()) {
                throw new Exception(response.getMessage() != null ? response.getMessage() : "查询失败");
            }

            List<Student> students = Jsonable.fromJson(Jsonable.toJson(response.getData()), new TypeToken<List<Student>>() {}.getType());
            if(students == null) {
                throw new Exception("信息解析失败");
            }

            return students;
        } catch (IOException e) {
            throw new Exception("无法连接至服务器");
        }
    }

    public boolean addStudent(List<Student> students) throws Exception {

        Message request = new Message(Message.ADD_STUDENT, students);
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if(response == null) {
                throw new Exception("服务器无响应");
            }

            if(!response.isSuccess()) {
                throw new Exception(response.getMessage() != null ? response.getMessage() : "查询失败");
            }

            return true;
        } catch (IOException e) {
            throw new Exception("无法连接至服务器");
        }
    }

}