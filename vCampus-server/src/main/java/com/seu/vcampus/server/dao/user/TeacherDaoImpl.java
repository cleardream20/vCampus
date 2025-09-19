package com.seu.vcampus.server.dao.user;

import com.seu.vcampus.common.model.Teacher;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.DBConnector;
import com.seu.vcampus.server.dao.UserDaoImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherDaoImpl implements TeacherDao {

    @Override
    public Teacher getTeacher(String cid) throws SQLException {
        String sql = "SELECT t.*, u.tname AS name, u.tsid, u.email, u.phone, u.role FROM tblTeacher t " +
                "JOIN tblUser u ON t.cid = u.cid WHERE t.cid = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Teacher teacher = new Teacher();
                    teacher.setCid(rs.getString("cid"));
                    teacher.setName(rs.getString("name"));
                    teacher.setPassword(""); // 敏感信息不返回
                    teacher.setTsid(rs.getString("tsid"));
                    teacher.setEmail(rs.getString("email"));
                    teacher.setPhone(rs.getString("phone"));
                    teacher.setRole(rs.getString("role"));
                    teacher.setAge(rs.getInt("age"));
                    teacher.setGender(rs.getString("gender"));
                    teacher.setAddress(rs.getString("address"));
                    teacher.setNid(rs.getString("nid"));

                    // 直接读取字符串类型的日期
                    teacher.setEndate(rs.getString("endate"));

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
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false);

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

            // 2. 插入 tblTeacher
            String sql = "INSERT INTO tblTeacher (cid, age, gender, address, nid, endate, title, department, curRole, modules) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, teacher.getCid());
                ps.setInt(2, teacher.getAge());
                ps.setString(3, teacher.getGender());
                ps.setString(4, teacher.getAddress());
                ps.setString(5, teacher.getNid());

                // 直接设置字符串日期
                ps.setString(6, teacher.getEndate());

                ps.setString(7, teacher.getTitle());
                ps.setString(8, teacher.getDepartment());
                ps.setString(9, teacher.getCurRole());
                ps.setString(10, teacher.getModules());

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
            System.err.println("教师信息冲突: " + ex.getMessage());
            throw ex;
        } catch (SQLException ex) {
            if (conn != null) conn.rollback();
            System.err.println("添加教师失败: " + ex.getMessage());
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
    public boolean updateTeacher(Teacher teacher) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false);

            System.out.println("尝试更新教师");

            // 1. 更新 tblUser 表
            String userSql = "UPDATE tblUser SET password = ?, tsid = ?, tname = ?, email = ?, phone = ?, role = ? WHERE cid = ?";
            try (PreparedStatement userPs = conn.prepareStatement(userSql)) {
                userPs.setString(1, teacher.getPassword());
                userPs.setString(2, teacher.getTsid());
                userPs.setString(3, teacher.getName());
                userPs.setString(4, teacher.getEmail());
                userPs.setString(5, teacher.getPhone());
                userPs.setString(6, teacher.getRole());
                userPs.setString(7, teacher.getCid());

                int userRows = userPs.executeUpdate();
                System.out.println("用户表更新影响行数: " + userRows);

                if (userRows == 0) {
                    conn.rollback();
                    return false;
                }
            }

            System.out.println("尝试更新教师2");

            // 2. 更新 tblTeacher 表
            String teacherSql = "UPDATE tblTeacher SET age = ?, gender = ?, address = ?, nid = ?, " +
                    "endate = ?, title = ?, department = ?, curRole = ?, modules = ? WHERE cid = ?";

            System.out.println("SQL: " + teacherSql);

            try (PreparedStatement teacherPs = conn.prepareStatement(teacherSql)) {
                teacherPs.setInt(1, teacher.getAge());
                teacherPs.setString(2, teacher.getGender());
                teacherPs.setString(3, teacher.getAddress());
                teacherPs.setString(4, teacher.getNid());

                // 直接设置字符串日期
                teacherPs.setString(5, teacher.getEndate());

                teacherPs.setString(6, teacher.getTitle());
                teacherPs.setString(7, teacher.getDepartment());
                teacherPs.setString(8, teacher.getCurRole());
                teacherPs.setString(9, teacher.getModules());
                teacherPs.setString(10, teacher.getCid());

                int teacherRows = teacherPs.executeUpdate();
                System.out.println("教师表更新影响行数: " + teacherRows);

                if (teacherRows == 0) {
                    System.out.println("教师记录不存在，尝试插入新记录");

                    // 插入新记录
                    String insertSql = "INSERT INTO tblTeacher (cid, age, gender, address, nid, endate, title, department, curRole, modules) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                        insertPs.setString(1, teacher.getCid());
                        insertPs.setInt(2, teacher.getAge());
                        insertPs.setString(3, teacher.getGender());
                        insertPs.setString(4, teacher.getAddress());
                        insertPs.setString(5, teacher.getNid());

                        // 直接设置字符串日期
                        insertPs.setString(6, teacher.getEndate());

                        insertPs.setString(7, teacher.getTitle());
                        insertPs.setString(8, teacher.getDepartment());
                        insertPs.setString(9, teacher.getCurRole());
                        insertPs.setString(10, teacher.getModules());

                        int insertRows = insertPs.executeUpdate();
                        System.out.println("插入教师记录影响行数: " + insertRows);

                        if (insertRows == 0) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
            }

            conn.commit();
            System.out.println("事务提交成功");
            return true;

        } catch (Exception ex) {
            System.err.println("更新教师时发生异常:");
            System.err.println("异常类型: " + ex.getClass().getName());
            System.err.println("错误信息: " + ex.getMessage());

            if (ex instanceof SQLException) {
                SQLException sqlEx = (SQLException) ex;
                System.err.println("SQL状态: " + sqlEx.getSQLState());
                System.err.println("错误代码: " + sqlEx.getErrorCode());
            }

            ex.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                    System.out.println("事务已回滚");
                } catch (SQLException e) {
                    System.err.println("回滚失败: " + e.getMessage());
                }
            }
            throw new SQLException("更新教师失败", ex);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                    System.out.println("数据库连接已关闭");
                } catch (SQLException e) {
                    System.err.println("关闭连接失败: " + e.getMessage());
                }
            }
        }
    }

    public boolean deleteTeacher(Teacher teacher) throws SQLException {
        return deleteTeacher(teacher.getCid());
    }

    @Override
    public boolean deleteTeacher(String cid) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false);

            // 1. 先删除 tblTeacher 表中的记录
            String teacherSql = "DELETE FROM tblTeacher WHERE cid = ?";
            try (PreparedStatement teacherPs = conn.prepareStatement(teacherSql)) {
                teacherPs.setString(1, cid);
                teacherPs.executeUpdate();
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
            System.err.println("删除教师失败: " + ex.getMessage());
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
    public List<Teacher> getAllTeachers() throws SQLException {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT t.*, u.tname AS name, u.tsid, u.email, u.phone, u.role FROM tblTeacher t " +
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

                // 直接读取字符串类型的日期
                teacher.setEndate(rs.getString("endate"));

                teacher.setTitle(rs.getString("title"));
                teacher.setDepartment(rs.getString("department"));
                teacher.setCurRole(rs.getString("curRole"));
                teacher.setModules(rs.getString("modules"));
                teachers.add(teacher);
            }
        } catch (SQLException ex) {
            System.err.println("查询所有教师失败: " + ex.getMessage());
            throw ex;
        }
        return teachers;
    }
}