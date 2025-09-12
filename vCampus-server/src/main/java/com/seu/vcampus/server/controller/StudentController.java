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

public class StudentController {

    private final StudentServiceImpl studentService = new StudentServiceImpl();

    public Message handleRequest(Message request) throws SQLException {
        String type = request.getType();

        switch (type) {
            case Message.AD_STUDENT :
                String cid = Jsonable.fromJson((String) request.getData(), String.class);
                Student student = studentService.getStudent(cid);
                if (student == null) {
                    return Message.fromData(type, false, null, "意外错误");
                } else {
                    return Message.fromData(type, true, student, "查询成功");
                }
            case Message.ST_STUDENT:
                HashMap<Integer, String> filter = Jsonable.fromJson((String) request.getData(), HashMap.class) ;
                List<Student> students = studentService.getAllStudents(filter);
                return Message.fromData(type, true, students, "查询成功");
            default:
                return Message.fromData(Message.STATUS_ERROR, false, null, "查询失败");
        }
    }
}