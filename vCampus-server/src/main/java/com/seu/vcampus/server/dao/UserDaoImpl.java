package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Admin;
import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.model.Teacher;
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
            ps.setString(1, cid);
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
    public User getUserByPhone(String phone) throws SQLException {
        String sql = "select * from tblUser where phone = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, phone);
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
    public User getUserByEmail(String email) throws SQLException {
        String sql = "select * from tblUser where email = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, email);
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
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 1. 添加用户到tblUser
            String userSql = "INSERT INTO tblUser (cid, password, tsid, tname, email, phone, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setString(1, user.getCid());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getTsid());
                ps.setString(4, user.getName());
                ps.setString(5, user.getEmail());
                ps.setString(6, user.getPhone());
                ps.setString(7, user.getRole());
                ps.executeUpdate();
            }

            // 2. 根据角色添加对应的详细信息
            String role = user.getRole();
            if ("ST".equals(role) && user instanceof Student) {
                addStudent(conn, (Student) user);
            } else if ("TC".equals(role) && user instanceof Teacher) {
                addTeacher(conn, (Teacher) user);
            } else if ("AD".equals(role) && user instanceof Admin) {
                addAdmin(conn, (Admin) user);
            } else {
                throw new SQLException("用户角色与类型不匹配");
            }

            conn.commit(); // 提交事务
            return true;

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // 回滚事务
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.err.println("添加用户失败: " + ex.getMessage());
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 1. 更新tblUser表
            String userSql = "UPDATE tblUser SET password = ?, tsid = ?, tname = ?, email = ?, phone = ?, role = ? WHERE cid = ?";
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setString(1, user.getPassword());
                ps.setString(2, user.getTsid());
                ps.setString(3, user.getName());
                ps.setString(4, user.getEmail());
                ps.setString(5, user.getPhone());
                ps.setString(6, user.getRole());
                ps.setString(7, user.getCid());
                ps.executeUpdate();
            }

            // 2. 根据角色更新对应的详细信息
            String role = user.getRole();
            if ("ST".equals(role) && user instanceof Student) {
                updateStudent(conn, (Student) user);
            } else if ("TC".equals(role) && user instanceof Teacher) {
                updateTeacher(conn, (Teacher) user);
            } else if ("AD".equals(role) && user instanceof Admin) {
                updateAdmin(conn, (Admin) user);
            }

            conn.commit(); // 提交事务
            return true;

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // 回滚事务
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.err.println("更新用户失败: " + ex.getMessage());
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean deleteUser(String cid) throws SQLException {
        // 先获取用户信息以确定角色
        User user = getUser(cid);
        if (user == null) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // 开启事务

            // 1. 根据角色删除对应的详细信息
            String role = user.getRole();
            if ("ST".equals(role)) {
                deleteStudent(conn, cid);
            } else if ("TC".equals(role)) {
                deleteTeacher(conn, cid);
            } else if ("AD".equals(role)) {
                deleteAdmin(conn, cid);
            }

            // 2. 删除用户基本信息
            String userSql = "DELETE FROM tblUser WHERE cid = ?";
            try (PreparedStatement ps = conn.prepareStatement(userSql)) {
                ps.setString(1, cid);
                ps.executeUpdate();
            }

            conn.commit(); // 提交事务
            return true;

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // 回滚事务
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.err.println("删除用户失败: " + ex.getMessage());
            throw ex;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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

    @Override
    public Student getStudentByUser(User user) throws SQLException {
        String cid = user.getCid();
        String sql = "select * from tblStudent where cid = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, cid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student(
                            user,
                            rs.getString("gender"),
                            rs.getString("birthday"),
                            rs.getString("address"),
                            rs.getString("nid"),
                            rs.getString("endate"),
                            rs.getString("grade"),
                            rs.getString("major"),
                            rs.getString("stid"),
                            rs.getString("es"),
                            rs.getString("esState"),
                            rs.getInt("age")
                    );
                    return student;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
        return null;
    }

    @Override
    public Teacher getTeacherByUser(User user) throws SQLException {
        String cid = user.getCid();
        String sql = "select * from tblTeacher where cid = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, cid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Teacher tc = new Teacher(
                            user,
                            rs.getInt("age"),
                            rs.getString("gender"),
                            rs.getString("address"),
                            rs.getString("nid"),
                            rs.getString("endate"),
                            rs.getString("title"),
                            rs.getString("department")
                    );
                    return tc;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
        return null;
    }

    @Override
    public Admin getAdminByUser(User user) throws SQLException {
        String cid = user.getCid();
        String sql = "select * from tblAdmin where cid = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, cid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin(
                            user,
                            rs.getString("modules")
                    );
                    return admin;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
        return null;
    }

    // 辅助方法：添加学生详细信息
    private void addStudent(Connection conn, Student student) throws SQLException {
        String sql = "INSERT INTO tblStudent (cid, gender, birthday, address, nid, endate, grade, major, stid, es, esState, age) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getCid());
            ps.setString(2, student.getSex());
            ps.setString(3, student.getBirthday());
            ps.setString(4, student.getAddress());
            ps.setString(5, student.getNid());
            ps.setString(6, student.getEndate());
            ps.setString(7, student.getGrade());
            ps.setString(8, student.getMajor());
            ps.setString(9, student.getStid());
            ps.setString(10, student.getEs());
            ps.setString(11, student.getEsState());
            ps.setInt(12, student.getAge());
            ps.executeUpdate();
        }
    }

    // 辅助方法：添加教师详细信息
    private void addTeacher(Connection conn, Teacher teacher) throws SQLException {
        String sql = "INSERT INTO tblTeacher (cid, age, gender, address, nid, endate, title, department) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, teacher.getCid());
            ps.setInt(2, teacher.getAge());
            ps.setString(3, teacher.getGender());
            ps.setString(4, teacher.getAddress());
            ps.setString(5, teacher.getNid());
            ps.setString(6, teacher.getEndate());
            ps.setString(7, teacher.getTitle());
            ps.setString(8, teacher.getDepartment());
            ps.executeUpdate();
        }
    }

    // 辅助方法：添加管理员详细信息
    private void addAdmin(Connection conn, Admin admin) throws SQLException {
        String sql = "INSERT INTO tblAdmin (cid, modules) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admin.getCid());
            ps.setString(2, admin.getModules());
            ps.executeUpdate();
        }
    }

    // 辅助方法：更新学生详细信息
    private void updateStudent(Connection conn, Student student) throws SQLException {
        String sql = "UPDATE tblStudent SET gender = ?, birthday = ?, address = ?, nid = ?, endate = ?, grade = ?, major = ?, stid = ?, es = ?, esState = ?, age = ? WHERE cid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getSex());
            ps.setString(2, student.getBirthday());
            ps.setString(3, student.getAddress());
            ps.setString(4, student.getNid());
            ps.setString(5, student.getEndate());
            ps.setString(6, student.getGrade());
            ps.setString(7, student.getMajor());
            ps.setString(8, student.getStid());
            ps.setString(9, student.getEs());
            ps.setString(10, student.getEsState());
            ps.setInt(11, student.getAge());
            ps.setString(12, student.getCid());
            ps.executeUpdate();
        }
    }

    // 辅助方法：更新教师详细信息
    private void updateTeacher(Connection conn, Teacher teacher) throws SQLException {
        String sql = "UPDATE tblTeacher SET age = ?, gender = ?, address = ?, nid = ?, endate = ?, title = ?, department = ? WHERE cid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacher.getAge());
            ps.setString(2, teacher.getGender());
            ps.setString(3, teacher.getAddress());
            ps.setString(4, teacher.getNid());
            ps.setString(5, teacher.getEndate());
            ps.setString(6, teacher.getTitle());
            ps.setString(7, teacher.getDepartment());
            ps.setString(8, teacher.getCid());
            ps.executeUpdate();
        }
    }

    // 辅助方法：更新管理员详细信息
    private void updateAdmin(Connection conn, Admin admin) throws SQLException {
        String sql = "UPDATE tblAdmin SET modules = ? WHERE cid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admin.getModules());
            ps.setString(2, admin.getCid());
            ps.executeUpdate();
        }
    }

    // 辅助方法：删除学生详细信息
    private void deleteStudent(Connection conn, String cid) throws SQLException {
        String sql = "DELETE FROM tblStudent WHERE cid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cid);
            ps.executeUpdate();
        }
    }

    // 辅助方法：删除教师详细信息
    private void deleteTeacher(Connection conn, String cid) throws SQLException {
        String sql = "DELETE FROM tblTeacher WHERE cid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cid);
            ps.executeUpdate();
        }
    }

    // 辅助方法：删除管理员详细信息
    private void deleteAdmin(Connection conn, String cid) throws SQLException {
        String sql = "DELETE FROM tblAdmin WHERE cid = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cid);
            ps.executeUpdate();
        }
    }
}