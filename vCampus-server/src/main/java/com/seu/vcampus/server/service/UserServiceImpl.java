package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.Admin;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.model.Teacher;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.server.dao.UserDaoImpl;
import com.seu.vcampus.server.dao.user.AdminDaoImpl;
import com.seu.vcampus.server.dao.user.TeacherDaoImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceImpl implements UserService {

    private final UserDaoImpl userDao = new UserDaoImpl();
    private final TeacherDaoImpl teacherDao = new TeacherDaoImpl();
    private final AdminDaoImpl adminDao = new AdminDaoImpl();
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

    @Override
    public Student getStudentByUser(User user) throws SQLException {
        return userDao.getStudentByUser(user);
    }

    @Override
    public Teacher getTeacherByUser(User user) throws SQLException {
        return userDao.getTeacherByUser(user);
    }

    @Override
    public Admin getAdminByUser(User user) throws SQLException {
        return userDao.getAdminByUser(user);
    }

    @Override
    public User getUserByEmail(String email) throws SQLException {
        return userDao.getUserByEmail(email);
    }

    @Override
    public User getUserByPhone(String phone) throws SQLException {
        return userDao.getUserByPhone(phone);
    }

    @Override
    public boolean updateTeacher(Teacher teacher) throws SQLException {
        return teacherDao.updateTeacher(teacher);
    }
}
