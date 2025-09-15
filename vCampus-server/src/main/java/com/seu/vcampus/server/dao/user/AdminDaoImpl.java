package com.seu.vcampus.server.dao.user;

import com.seu.vcampus.common.model.Admin;
import com.seu.vcampus.common.model.User;

import java.sql.SQLException;
import java.util.List;

public class AdminDaoImpl implements AdminDao {

    @Override
    public Admin getAdmin(String cid) throws SQLException {
        return null;
    }

    @Override
    public Admin getAdmin(User user) throws SQLException {
        return null;
    }

    @Override
    public boolean addAdmin(Admin admin) throws SQLException {
        return false;
    }

    @Override
    public boolean updateAdmin(Admin admin) throws SQLException {
        return false;
    }

    @Override
    public boolean deleteAdmin(Admin admin) throws SQLException {
        return false;
    }

    @Override
    public List<Admin> getAllAdmins() throws SQLException {
        return List.of();
    }
}
