package com.seu.vcampus.server.controller;

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

public class StudentController {

    private final StudentServiceImpl studentService = new StudentServiceImpl();

    public Message handleRequest(Message request) throws SQLException {
        String type = request.getType();

        switch (type) {
            case Message.ST_STUDENT :
                Map<String, Object> data = Jsonable.fromJson(Jsonable.toJson(request.getData()) , Map.class);
                String cid = (String) data.get("cid");
                Student student = studentService.getStudent(cid);
//                System.out.println(request.getData());
//                String tmp = Jsonable.toJson(request.getData());
//                String cid = Jsonable.fromJson(tmp, String.class);
//                Student student = studentService.getStudent(cid);
                if (student == null) {
                    return Message.fromData(type, false, null, "意外错误");
                } else {
                    return Message.fromData(type, true, student, "查询成功");
                }
            case Message.AD_STUDENT:
                HashMap<Integer, String> filter = Jsonable.fromJson(Jsonable.toJson(request.getData()), HashMap.class) ;
                List<Student> students = studentService.getAllStudents(filter);
                return Message.fromData(type, true, students, "查询成功");
            default:
                return Message.fromData(Message.STATUS_ERROR, false, null, "查询失败");
        }
    }
}