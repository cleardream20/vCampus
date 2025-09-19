package com.seu.vcampus.server.dao.user;

import com.seu.vcampus.common.model.Teacher;
import com.seu.vcampus.common.model.User;

import java.sql.SQLException;
import java.util.List;

public interface TeacherDao {
    /**
     * 通过cid寻找教师
     * @param cid 一卡通号
     * @return teacher 寻找到的teacher
     * @throws SQLException 数据库错误
     */
    Teacher getTeacher(String cid) throws SQLException;

    /**
     * user转换成具体的teacher(by cid)
     * @param user 登录的user
     * @return teacher 找到的teacher
     * @throws SQLException 数据库错误
     */
    Teacher getTeacher(User user) throws SQLException;

    /**
     * 增加教师
     * @param teacher 要增加的教师
     * @return successful or not
     * @throws SQLException 数据库错误
     */
    boolean addTeacher(Teacher teacher) throws SQLException;

    /**
     * 更新教师信息
     * @param teacher 待更新的教师
     * @return successful or not
     * @throws SQLException 数据库错误
     */
    boolean updateTeacher(Teacher teacher) throws SQLException;

    /**
     * 删除教师信息
     * @param teacher 待删除的教师
     * @return successful or not
     * @throws SQLException 数据库错误
     */
    boolean deleteTeacher(String cid) throws SQLException;

    /**
     * 获取所有教师信息
     * @return all teachers
     * @throws SQLException 数据库错误
     */
    List<Teacher> getAllTeachers() throws SQLException;
}
