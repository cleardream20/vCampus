package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.User;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    /**
     * 根据唯一id cid查找用户
     * @param cid 一卡通号
     * @return user, or null(if not found)
     * @throws SQLException 数据库错误
     */
    User getUser(String cid) throws SQLException;

    /**
     * 添加新用户
     * @param user 用户对象
     * @return successful or not
     * @throws SQLException
     */
    boolean addUser(User user) throws SQLException;

    /**
     * 更新用户
     * @param user
     * @return successful or not
     * @throws SQLException
     */
    boolean updateUser(User user) throws SQLException;

    /**
     * 删除用户
     * @param cid
     * @return successful or not
     * @throws SQLException
     */
    boolean deleteUser(String cid) throws SQLException;

    /**
     * 获取所有用户
     * @return list of users 用户列表
     * @throws SQLException
     */
    List<User> getAllUsers() throws SQLException;
}
