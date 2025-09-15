package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.DBConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    @Override
    public User getUser(String cid) throws SQLException {
        String sql = "select * from tblUser where cid = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1,cid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("cid"),
                            rs.getString("password"),
                            rs.getString("tsid"),
                            rs.getString("tname"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("role")
                    );
                    // 记得return!
                    return user;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }

        return null;
    }

    @Override
    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO tblUser (cid, password, tsid, tname, email, phone, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1,  user.getCid());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getTsid());
            ps.setString(4, user.getName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getRole());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLIntegrityConstraintViolationException ex) {
            System.err.println("用户已存在: " + user.getName());
            throw ex;
        } catch (SQLException ex) {
            System.err.println("添加用户失败: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE tblUser SET password = ?, tsid = ?, tname = ?, email = ?, phone = ?, role = ? WHERE cid = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getTsid());
            ps.setString(3, user.getName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getRole());
            ps.setString(7, user.getCid());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            System.err.println("更新用户失败: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public boolean deleteUser(String cid) throws SQLException {
        String sql = "DELETE FROM tblUser WHERE cid = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, cid);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            System.err.println("删除用户失败: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM tblUser";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("cid"),
                        rs.getString("password"),
                        rs.getString("tsid"),
                        rs.getString("tname"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException ex) {
            System.err.println("查询所有用户失败: " + ex.getMessage());
            throw ex;
        }

        return users;
    }
}
