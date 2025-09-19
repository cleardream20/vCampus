package com.seu.vcampus.server.service.user;

import com.seu.vcampus.common.model.Admin;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.server.dao.user.AdminDao;
import com.seu.vcampus.server.dao.user.AdminDaoImpl;

import java.sql.SQLException;
import java.util.List;

public class AdminServiceImpl implements AdminService {
    private final AdminDao adminDao =  new AdminDaoImpl();

    @Override
    public Admin getAdmin(String cid) throws SQLException {
        return adminDao.getAdmin(cid);
    }

    @Override
    public Admin getAdmin(User user) throws SQLException {
        return adminDao.getAdmin(user);
    }

    @Override
    public boolean addAdmin(Admin admin) throws SQLException {
        return adminDao.addAdmin(admin);
    }

    @Override
    public boolean updateAdmin(Admin admin) throws SQLException {
        return adminDao.updateAdmin(admin);
    }

    @Override
    public boolean deleteAdmin(String cid) throws SQLException {
        return adminDao.deleteAdmin(cid);
    }

    @Override
    public List<Admin> getAllAdmins() throws SQLException {
        return adminDao.getAllAdmins();
    }
}
