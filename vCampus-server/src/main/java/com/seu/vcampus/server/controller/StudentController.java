package com.seu.vcampus.server.controller;

import com.google.gson.reflect.TypeToken;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.Jsonable;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.server.service.StudentServiceImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StudentController implements RequestController {

    private final StudentServiceImpl studentService = new StudentServiceImpl();

    public Message handleRequest(Message request) throws SQLException {
        String type = request.getType();

        switch (type) {
            case Message.ST_STUDENT :
                String cid = Jsonable.fromJson(Jsonable.toJson(request.getData()) , String.class);
                Student student = studentService.getStudent(cid);
                if (student == null) {
                    return Message.fromData(type, false, null, "意外错误");
                } else {
                    return Message.fromData(type, true, student, "查询成功");
                }
            case Message.AD_STUDENT:
                HashMap<Integer, String> filter = Jsonable.fromJson(Jsonable.toJson(request.getData()) , new TypeToken<HashMap<Integer, String>>() {}.getType());
                List<Student> students = studentService.getAllStudents(filter);
                return Message.fromData(type, true, students, "查询成功");
            case Message.ADD_STUDENT:
                List<Student> toadd = Jsonable.fromJson(Jsonable.toJson(request.getData()), new TypeToken<List<Student>>() {}.getType());
                boolean result = studentService.addStudent(toadd);
                return Message.fromData(type, result, result, result ? "添加成功" : "添加失败");
            default:
                return Message.fromData(Message.STATUS_ERROR, false, null, "查询失败");
        }
    }
}