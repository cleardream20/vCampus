package com.seu.vcampus.server.service.user;

import com.seu.vcampus.common.model.Admin;
import com.seu.vcampus.common.model.User;

import java.sql.SQLException;
import java.util.List;

public interface AdminService {
    /**
     * 通过cid获取admin信息
     * @param cid 一卡通号
     * @return 对应的admin
     * @throws SQLException 数据库异常
     */
    Admin getAdmin(String cid)  throws SQLException;

    /**
     * 通过user获取admin信息
     * @param user 登录的user
     * @return 对应的admin
     * @throws SQLException 数据库异常
     */
    Admin getAdmin(User user)  throws SQLException;

    /**
     * 增加一个管理员
     * @param admin 要增加的管理员
     * @return successful or not
     * @throws SQLException 数据库异常
     */
    boolean addAdmin(Admin admin)  throws SQLException;

    /**
     * 更新管理员信息，只有拥有最高权限（modules == "All"）的管理层才可进行该操作
     * @param admin 要更新的管理员
     * @return successful or not
     * @throws SQLException 数据库异常
     */
    boolean updateAdmin(Admin admin)  throws SQLException;

    /**
     * 删除管理员，不允许删除自己，只有有相关权限的管理员才可进行该操作
     * @param cid 要删除的管理员
     * @return successful or not
     * @throws SQLException 数据库异常
     */
    boolean deleteAdmin(String cid)  throws SQLException;

    /**
     * 查看所有管理员信息
     * @return all admins
     * @throws SQLException 数据库异常
     */
    List<Admin> getAllAdmins() throws SQLException;
}
