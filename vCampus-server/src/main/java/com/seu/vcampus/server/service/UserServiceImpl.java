package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.User;
import com.seu.vcampus.server.dao.UserDaoImpl;

import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService {

    private final UserDaoImpl userDao = new UserDaoImpl();

    @Override
    public User getUser(String cid) throws SQLException {
        return userDao.getUser(cid);
    }

    @Override
    public boolean addUser(User user) throws SQLException {
        if(user.getCid() == null || user.getCid().trim().isEmpty()) {
//            System.err.println("Cid is null or empty");
            throw new SQLException("Cid is null or empty");
        }
        if(getUser(user.getCid()) != null) {
//            System.err.println("User already exists");
            throw new SQLException("User already exists");
        }
        if(user.getPassword() == null || user.getPassword().trim().isEmpty() || user.getPassword().length() < 6) {
            throw new SQLException("Password is not compliant");
        }
        return userDao.addUser(user);
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        User existingUser = getUser(user.getCid());
        if(existingUser == null) {
//            System.err.println("Invalid CID" + user.getCid());
            throw  new SQLException("Invalid CID" + user.getCid());
        }
        return userDao.updateUser(user);
    }

    @Override
    public boolean deleteUser(String cid) throws SQLException {
        if(getUser(cid) == null) {
            throw  new SQLException("Invalid CID" + cid);
        }
        return userDao.deleteUser(cid);
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        return userDao.getAllUsers();
    }

    @Override
    public User Login(String cid, String password) throws SQLException {
        User user = userDao.getUser(cid);
        System.out.println("获取用户成功！");
        if(user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public User Register(User user) throws SQLException {
        boolean success = addUser(user); // 复用addUser()
        if(success) {
            return user;
        }
        return null;
    }
}
