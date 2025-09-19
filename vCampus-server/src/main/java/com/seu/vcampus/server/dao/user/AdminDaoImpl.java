package com.seu.vcampus.server.dao.user;

import com.seu.vcampus.common.model.Admin;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.DBConnector;
import com.seu.vcampus.server.dao.UserDaoImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDaoImpl implements AdminDao {

    @Override
    public Admin getAdmin(String cid) throws SQLException {
        String sql = "SELECT a.*, u.tname AS name, u.tsid, u.email, u.phone, u.role FROM tblAdmin a " +
                "JOIN tblUser u ON a.cid = u.cid WHERE a.cid = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setCid(rs.getString("cid"));
                    admin.setName(rs.getString("name"));
                    admin.setPassword(""); // 敏感信息不返回
                    admin.setTsid(rs.getString("tsid"));
                    admin.setEmail(rs.getString("email"));
                    admin.setPhone(rs.getString("phone"));
                    admin.setRole(rs.getString("role"));
                    admin.setModules(rs.getString("modules"));
                    return admin;
                }
            }
        } catch (SQLException ex) {
            System.err.println("查询管理员失败: " + ex.getMessage());
            throw ex;
        }
        return null;
    }

    @Override
    public Admin getAdmin(User user) throws SQLException {
        return getAdmin(user.getCid());
    }

    @Override
    public boolean addAdmin(Admin admin) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false);

            // 1. 先插入到 tblUser（如果用户还不存在）
            UserDaoImpl userDao = new UserDaoImpl();
            User user = new User(
                    admin.getCid(),
                    admin.getPassword(),
                    admin.getTsid(),
                    admin.getName(),
                    admin.getEmail(),
                    admin.getPhone(),
                    admin.getRole()
            );

            boolean userExists = userDao.getUser(admin.getCid()) != null;
            if (!userExists) {
                if (!userDao.addUser(user)) {
                    conn.rollback();
                    return false;
                }
            } else {
                // 如果已存在，更新基础信息
                if (!userDao.updateUser(user)) {
                    conn.rollback();
                    return false;
                }
            }

            // 2. 插入 tblAdmin
            String sql = "INSERT INTO tblAdmin (cid, modules) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, admin.getCid());
                ps.setString(2, admin.getModules());

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLIntegrityConstraintViolationException ex) {
            if (conn != null) conn.rollback();
            System.err.println("管理员信息冲突: " + ex.getMessage());
            throw ex;
        } catch (SQLException ex) {
            if (conn != null) conn.rollback();
            System.err.println("添加管理员失败: " + ex.getMessage());
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("关闭连接失败: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean updateAdmin(Admin admin) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false);

            // 1. 更新 tblUser 表
            String userSql = "UPDATE tblUser SET password = ?, tsid = ?, tname = ?, email = ?, phone = ?, role = ? WHERE cid = ?";
            try (PreparedStatement userPs = conn.prepareStatement(userSql)) {
                userPs.setString(1, admin.getPassword());
                userPs.setString(2, admin.getTsid());
                userPs.setString(3, admin.getName());
                userPs.setString(4, admin.getEmail());
                userPs.setString(5, admin.getPhone());
                userPs.setString(6, admin.getRole());
                userPs.setString(7, admin.getCid());

                int userRows = userPs.executeUpdate();
                if (userRows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 2. 更新 tblAdmin 表
            String adminSql = "UPDATE tblAdmin SET modules = ? WHERE cid = ?";
            try (PreparedStatement adminPs = conn.prepareStatement(adminSql)) {
                adminPs.setString(1, admin.getModules());
                adminPs.setString(2, admin.getCid());

                int adminRows = adminPs.executeUpdate();
                if (adminRows == 0) {
                    // 管理员记录不存在，尝试插入新记录
                    String insertSql = "INSERT INTO tblAdmin (cid, modules) VALUES (?, ?)";
                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setString(1, admin.getCid());
                        insertPs.setString(2, admin.getModules());

                        int insertRows = insertPs.executeUpdate();
                        if (insertRows == 0) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
            }

            conn.commit();
            return true;

        } catch (Exception ex) {
            System.err.println("更新管理员时发生异常:");
            System.err.println("异常类型: " + ex.getClass().getName());
            System.err.println("错误信息: " + ex.getMessage());

            if (ex instanceof SQLException) {
                SQLException sqlEx = (SQLException) ex;
                System.err.println("SQL状态: " + sqlEx.getSQLState());
                System.err.println("错误代码: " + sqlEx.getErrorCode());
            }

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    System.err.println("回滚失败: " + e.getMessage());
                }
            }
            throw new SQLException("更新管理员失败", ex);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("关闭连接失败: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public boolean deleteAdmin(String cid) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false);

            // 1. 先删除 tblAdmin 表中的记录
            String adminSql = "DELETE FROM tblAdmin WHERE cid = ?";
            try (PreparedStatement adminPs = conn.prepareStatement(adminSql)) {
                adminPs.setString(1, cid);
                adminPs.executeUpdate();
            }

            // 2. 再删除 tblUser 表中的记录
            String userSql = "DELETE FROM tblUser WHERE cid = ?";
            try (PreparedStatement userPs = conn.prepareStatement(userSql)) {
                userPs.setString(1, cid);
                userPs.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    System.err.println("回滚失败: " + e.getMessage());
                }
            }
            System.err.println("删除管理员失败: " + ex.getMessage());
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("关闭连接失败: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public List<Admin> getAllAdmins() throws SQLException {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT a.*, u.tname AS name, u.tsid, u.email, u.phone, u.role FROM tblAdmin a " +
                "JOIN tblUser u ON a.cid = u.cid";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Admin admin = new Admin();
                admin.setCid(rs.getString("cid"));
                admin.setName(rs.getString("name"));
                admin.setTsid(rs.getString("tsid"));
                admin.setEmail(rs.getString("email"));
                admin.setPhone(rs.getString("phone"));
                admin.setRole(rs.getString("role"));
                admin.setModules(rs.getString("modules"));
                admins.add(admin);
            }
        } catch (SQLException ex) {
            System.err.println("查询所有管理员失败: " + ex.getMessage());
            throw ex;
        }
        return admins;
    }
}