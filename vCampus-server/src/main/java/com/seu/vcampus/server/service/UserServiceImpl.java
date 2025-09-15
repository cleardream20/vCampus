package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.User;
import com.seu.vcampus.server.dao.UserDaoImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceImpl implements UserService {

    private final UserDaoImpl userDao = new UserDaoImpl();
    private static final ConcurrentHashMap<String, User> onlineUsers = new ConcurrentHashMap<String, User>();

    @Override
    public User getUser(String cid) throws SQLException {
        return userDao.getUser(cid);
    }

    @Override
    public boolean addUser(User user) throws SQLException {
        if(user.getCid() == null || user.getCid().trim().isEmpty()) {
            throw new SQLException("Cid is null or empty");
        }
        if(getUser(user.getCid()) != null) {
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
            throw new SQLException("Invalid CID" + user.getCid());
        }
        return userDao.updateUser(user);
    }

    @Override
    public boolean deleteUser(String cid) throws SQLException {
        if(getUser(cid) == null) {
            throw new SQLException("Invalid CID" + cid);
        }
        return userDao.deleteUser(cid);
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        return userDao.getAllUsers();
    }

    @Override
    public User login(String cid, String password) throws SQLException {
        User user = userDao.getUser(cid);
        if(user != null && user.getPassword().equals(password)) {
            onlineUsers.put(cid, user);
            return user;
        }
        return null;
    }

    @Override
    public User register(User user) throws SQLException {
        boolean success = addUser(user); // 复用addUser()
        if(success) return user;
        return null;
    }

    @Override
    public boolean checkOnlineUser(String cid) {
        return onlineUsers.containsKey(cid);
    }

    @Override
    public void logout(String cid) throws SQLException {
        onlineUsers.remove(cid);
    }
}
