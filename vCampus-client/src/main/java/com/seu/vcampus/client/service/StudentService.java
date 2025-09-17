package com.seu.vcampus.client.service;

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

        Student _student = new Student();
        _student.setCid(cid);

        Message request = new Message(Message.ST_STUDENT, _student);
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

    public List getDataWithFilters(HashMap<Integer, String> filters) throws Exception {

        Message request = new Message(Message.AD_STUDENT, filters);
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if(response == null) {
                throw new Exception("服务器无响应");
            }

            if(!response.isSuccess()) {
                throw new Exception(response.getMessage() != null ? response.getMessage() : "查询失败");
            }

            List students = Jsonable.fromJson(Jsonable.toJson(response.getData()), List.class);
            if(students == null) {
                throw new Exception("信息解析失败");
            }

            return students;
        } catch (IOException e) {
            throw new Exception("无法连接至服务器");
        }
    }

}