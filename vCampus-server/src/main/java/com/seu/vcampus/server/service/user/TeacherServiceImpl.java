package com.seu.vcampus.server.service.user;

import com.seu.vcampus.common.model.Teacher;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.server.dao.user.TeacherDao;
import com.seu.vcampus.server.dao.user.TeacherDaoImpl;

import java.sql.SQLException;
import java.util.List;

public class TeacherServiceImpl implements TeacherService {
    private final TeacherDao teacherDao = new TeacherDaoImpl();

    @Override
    public Teacher getTeacher(String cid) throws SQLException {
        return teacherDao.getTeacher(cid);
    }

    @Override
    public Teacher getTeacher(User user) throws SQLException {
        return teacherDao.getTeacher(user);
    }

    @Override
    public boolean addTeacher(Teacher teacher) throws SQLException {
        return teacherDao.addTeacher(teacher);
    }

    @Override
    public boolean updateTeacher(Teacher teacher) throws SQLException {
        return teacherDao.updateTeacher(teacher);
    }

    @Override
    public boolean deleteTeacher(String cid) throws SQLException {
        return teacherDao.deleteTeacher(cid);
    }

    @Override
    public List<Teacher> getAllTeachers() throws SQLException {
        return teacherDao.getAllTeachers();
    }
}
