package com.seu.vcampus.server.dao.user;

import com.seu.vcampus.common.model.Teacher;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.DBConnector;
import com.seu.vcampus.server.dao.UserDaoImpl;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TeacherDaoImpl implements TeacherDao {

    // 日期格式：数据库中使用 'yyyy-MM-dd'
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Teacher getTeacher(String cid) throws SQLException {
        String sql = "SELECT t.*, u.tname AS name FROM tblTeacher t " +
                "JOIN tblUser u ON t.cid = u.cid WHERE t.cid = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 从 User 表获取 name，其他字段从 Teacher 表获取
                    Teacher teacher = new Teacher();
                    teacher.setCid(rs.getString("cid"));
                    teacher.setName(rs.getString("name")); // 来自 User
                    teacher.setPassword(""); // 敏感信息不返回，或从 User 查询
                    teacher.setTsid(rs.getString("tsid"));
                    teacher.setEmail(rs.getString("email"));
                    teacher.setPhone(rs.getString("phone"));
                    teacher.setRole(rs.getString("role"));
                    teacher.setAge(rs.getInt("age"));
                    teacher.setGender(rs.getString("gender"));
                    teacher.setAddress(rs.getString("address"));
                    teacher.setNid(rs.getString("nid"));
                    teacher.setEndate(rs.getString("endate")); // 假设是字符串格式
                    teacher.setTitle(rs.getString("title"));
                    teacher.setDepartment(rs.getString("department"));
                    teacher.setCurRole(rs.getString("curRole"));
                    teacher.setModules(rs.getString("modules"));
                    return teacher;
                }
            }
        } catch (SQLException ex) {
            System.err.println("查询教师失败: " + ex.getMessage());
            throw ex;
        }
        return null;
    }

    @Override
    public Teacher getTeacher(User user) throws SQLException {
        return getTeacher(user.getCid());
    }

    @Override
    public boolean addTeacher(Teacher teacher) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 1. 先插入到 tblUser（如果用户还不存在）
            UserDaoImpl userDao = new UserDaoImpl();
            User user = new User(
                    teacher.getCid(),
                    teacher.getPassword(),
                    teacher.getTsid(),
                    teacher.getName(),
                    teacher.getEmail(),
                    teacher.getPhone(),
                    teacher.getRole()
            );
            boolean userExists = userDao.getUser(teacher.getCid()) != null;
            if (!userExists) {
                boolean userAdded = userDao.addUser(user);
                if (!userAdded) {
                    conn.rollback();
                    return false;
                }
            } else {
                // 如果已存在，更新基础信息（可选）
                userDao.updateUser(user);
            }

            // 2. 插入 tblTeacher
            String sql = "INSERT INTO tblTeacher (cid, age, gender, address, nid, endate, title, department, curRole, modules) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, teacher.getCid());
            ps.setInt(2, teacher.getAge());
            ps.setString(3, teacher.getGender());
            ps.setString(4, teacher.getAddress());
            ps.setString(5, teacher.getNid());
            ps.setDate(6, java.sql.Date.valueOf(LocalDate.parse(teacher.getEndate(), DATE_FORMATTER)));
            ps.setString(7, teacher.getTitle());
            ps.setString(8, teacher.getDepartment());
            ps.setString(9, teacher.getCurRole());
            ps.setString(10, teacher.getModules());

            int rows = ps.executeUpdate();
            conn.commit();
            return rows > 0;

        } catch (SQLIntegrityConstraintViolationException ex) {
            if (conn != null) conn.rollback();
            System.err.println("教师信息冲突（如身份证重复）: " + ex.getMessage());
            throw ex;
        } catch (SQLException ex) {
            if (conn != null) conn.rollback();
            System.err.println("添加教师失败: " + ex.getMessage());
            throw ex;
        } finally {
            if (ps != null) ps.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public boolean updateTeacher(Teacher teacher) throws SQLException {
        String sql = "UPDATE tblTeacher SET age = ?, gender = ?, address = ?, nid = ?, " +
                "endate = ?, title = ?, department = ? curRole = ? modules = ? WHERE cid = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, teacher.getAge());
            ps.setString(2, teacher.getGender());
            ps.setString(3, teacher.getAddress());
            ps.setString(4, teacher.getNid());
            ps.setDate(5, java.sql.Date.valueOf(LocalDate.parse(teacher.getEndate(), DATE_FORMATTER)));
            ps.setString(6, teacher.getTitle());
            ps.setString(7, teacher.getDepartment());
            ps.setString(8, teacher.getCid());
            ps.setString(9, teacher.getCurRole());
            ps.setString(10, teacher.getModules());

            int rows = ps.executeUpdate();

            // 同时更新 User 基本信息
            if (rows > 0) {
                UserDaoImpl userDao = new UserDaoImpl();
                User user = new User(
                        teacher.getCid(),
                        teacher.getPassword(),
                        teacher.getTsid(),
                        teacher.getName(),
                        teacher.getEmail(),
                        teacher.getPhone(),
                        teacher.getRole()
                );
                userDao.updateUser(user);
            }

            return rows > 0;
        } catch (SQLException ex) {
            System.err.println("更新教师失败: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public boolean deleteTeacher(Teacher teacher) throws SQLException {
        return deleteTeacher(teacher.getCid());
    }

    public boolean deleteTeacher(String cid) throws SQLException {
        String sql = "DELETE FROM tblTeacher WHERE cid = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cid);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            System.err.println("删除教师失败: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<Teacher> getAllTeachers() throws SQLException {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT t.*, u.tname AS name FROM tblTeacher t " +
                "JOIN tblUser u ON t.cid = u.cid";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Teacher teacher = new Teacher();
                teacher.setCid(rs.getString("cid"));
                teacher.setName(rs.getString("name"));
                teacher.setTsid(rs.getString("tsid"));
                teacher.setEmail(rs.getString("email"));
                teacher.setPhone(rs.getString("phone"));
                teacher.setRole(rs.getString("role"));
                teacher.setAge(rs.getInt("age"));
                teacher.setGender(rs.getString("gender"));
                teacher.setAddress(rs.getString("address"));
                teacher.setNid(rs.getString("nid"));
                teacher.setEndate(rs.getDate("endate").toLocalDate().format(DATE_FORMATTER));
                teacher.setTitle(rs.getString("title"));
                teacher.setDepartment(rs.getString("department"));
                teacher.setCurRole("curRole");
                teacher.setModules("modules");
                teachers.add(teacher);
            }
        } catch (SQLException ex) {
            System.err.println("查询所有教师失败: " + ex.getMessage());
            throw ex;
        }
        return teachers;
    }
}