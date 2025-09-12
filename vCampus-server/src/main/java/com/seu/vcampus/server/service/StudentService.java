package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;

public interface StudentService {
    /**
     * 根据cid查询学生
     * @param cid 用户唯一标识
     * @return Student 所查询的学生
     * @throws SQLException sql错误
     */
    Student getStudent(String cid) throws SQLException;


    /**
     * @param filters 获取符合条件的学生
     * @return List<Student> 用户列表
     * @throws SQLException sql错误
     */
    List<Student> getAllUsers(HashMap<Integer, String> filters) throws SQLException;

}
