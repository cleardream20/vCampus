package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.Admin;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.model.Teacher;
import com.seu.vcampus.common.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserService {
    /**
     * 根据cid查询用户
     * @param cid 用户唯一标识
     * @return user 所查询的用户
     * @throws SQLException 数据库查询出错时
     */
    User getUser(String cid) throws SQLException;

    /**
     * 添加用户
     * @param user 将要添加的用户
     * @return successful or not
     * @throws SQLException 数据库查询出错时
     */
    boolean addUser(User user) throws SQLException;

    /**
     * 更新用户
     * @param user 将要更新的用户
     * @return successful or not
     * @throws SQLException 数据库查询出错时
     */
    boolean updateUser(User user) throws SQLException;

    /**
     * 删除用户
     * @param cid 用户唯一标识
     * @return successful or not
     * @throws SQLException 数据库查询出错时
     */
    boolean deleteUser(String cid) throws SQLException;

    /**
     * 获取所有用户
     * @return List<User> 用户列表
     * @throws SQLException 数据库查询出错时
     */
    List<User> getAllUsers() throws SQLException;

    /**
     * 用户登录
     * @param cid 用户唯一标识
     * @param password 密码
     * @return 成功登录的user
     * @throws SQLException 数据库查询出错时
     */
    User login(String cid, String password) throws SQLException;

    /**
     * 用户注册
     * @param user 将要注册的user
     * @return 成功注册的user
     * @throws SQLException 数据库查询出错时
     */
    User register(User user) throws SQLException;

    /**
     * 检查用户是否已登录
     * @param cid 用户cid
     * @return 是否已登录
     */
    boolean checkOnlineUser(String cid);

    void logout(String cid) throws SQLException;

    Student getStudentByUser(User user) throws  SQLException;

    Teacher getTeacherByUser(User user) throws  SQLException;

    Admin getAdminByUser(User user) throws  SQLException;

    User getUserByEmail(String email) throws SQLException;

    User getUserByPhone(String phone) throws SQLException;

    boolean updateTeacher(Teacher teacher) throws  SQLException;
}
