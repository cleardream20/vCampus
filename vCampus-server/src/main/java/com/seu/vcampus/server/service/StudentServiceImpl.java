package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.server.dao.StudentDaoImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;

public class StudentServiceImpl implements StudentService {

    private final StudentDaoImpl studentDao = new StudentDaoImpl();

    @Override
    public Student getStudent(String cid) throws SQLException {
        if (cid == null) {
            throw new SQLException("Cid is null");
        }
        return studentDao.getStudent(cid);
    }

    @Override
    public List<Student> getAllStudents(HashMap<Integer, String> filters) throws SQLException{
        return studentDao.getStudents(filters);
    }
}
