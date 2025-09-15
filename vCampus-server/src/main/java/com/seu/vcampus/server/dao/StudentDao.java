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

    /**
     * 根据给出的筛选条件
     * @param filters 筛选条件字典，格式为HashMap<Integer, String>;
     *                下标顺序对应为一卡通号、身份证号、学号、姓名、性别、电话号码、出生日期、家庭住址、入学日期、学籍号、学院、年级、学制、学籍状态
     * @return List<Student>
     * @throws SQLException 数据库错误
     */
    List<Student> getStudents(HashMap<Integer, String> filters) throws SQLException;

}
