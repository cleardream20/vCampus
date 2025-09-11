package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Student;
import java.util.List;
import java.sql.SQLException;
import java.util.HashMap;

public interface StudentDao {
    /**
     * 根据唯一id cid查找用户
     * @param cid 一卡通号（用户唯一标识）
     * @return Student, or null(if not found)
     * @throws SQLException 数据库错误
     */
    Student getStudent(String cid) throws SQLException;

    List<Student> getStudents(HashMap<Integer, String> map) throws SQLException;

}
