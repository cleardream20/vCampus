package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Admin;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.model.Teacher;
import com.seu.vcampus.common.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    /**
     * 根据唯一id cid查找用户
     * @param cid 一卡通号（用户唯一标识）
     * @return user, or null(if not found)
     * @throws SQLException 数据库错误
     */
    User getUser(String cid) throws SQLException;

    User getUserByPhone(String phone) throws SQLException;

    User getUserByEmail(String email) throws SQLException;

    /**
     * 添加新用户
     * @param user 将要新增的用户
     * @return successful or not
     * @throws SQLException 数据库错误
     */
    boolean addUser(User user) throws SQLException;

    /**
     * 更新用户
     * @param user 将要更新的用户
     * @return successful or not
     * @throws SQLException 数据库错误
     */
    boolean updateUser(User user) throws SQLException;

    /**
     * 删除用户
     * @param cid 用户唯一标识
     * @return successful or not
     * @throws SQLException 数据库错误
     */
    boolean deleteUser(String cid) throws SQLException;

    /**
     * 获取所有用户
     * @return list of users 用户列表
     * @throws SQLException 数据库错误
     */
    List<User> getAllUsers() throws SQLException;

    Student getStudentByUser(User user) throws  SQLException;

    Teacher getTeacherByUser(User user) throws  SQLException;

    Admin getAdminByUser(User user) throws  SQLException;
}
