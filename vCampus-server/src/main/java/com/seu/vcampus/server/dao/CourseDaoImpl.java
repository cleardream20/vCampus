package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.course.*;
import com.seu.vcampus.common.util.DBConnector;
import com.seu.vcampus.server.dao.CourseDao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CourseDaoImpl implements CourseDao {

    @Override
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT courseId, courseName, teacherName, teacherId, department, credit, schedule, location, capacity, selectedNum, StartWeek, endweek FROM Courses";

        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(mapRowToCourse(rs));
            }
            System.out.println("成功获取 " + courses.size() + " 门课程");
        } catch (SQLException e) {
            System.err.println("获取课程列表失败: " + e.getMessage());
            throw new RuntimeException("获取课程列表失败", e);
        }
        return courses;
    }

    @Override
    public int addCourse(Course course) {
        String sql = "INSERT INTO Courses (CourseID, CourseName, TeacherID, TeacherName, Department, Credit, Schedule, Location, Capacity, SelectedNum, StartWeek, EndWeek) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getCourseId());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getTeacherId());
            pstmt.setString(4, course.getTeacherName());
            pstmt.setString(5, course.getDepartment());
            pstmt.setInt(6, course.getCredit());
            pstmt.setString(7, course.getSchedule());
            pstmt.setString(8, course.getLocation());
            pstmt.setInt(9, course.getCapacity());
            pstmt.setInt(10, course.getSelectedNum());
            pstmt.setInt(11, course.getStartWeek());
            pstmt.setInt(12, course.getEndWeek());

            int result = pstmt.executeUpdate();
            System.out.println("添加课程结果: " + result);
            return result;
        } catch (SQLException e) {
            System.err.println("添加课程失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("添加课程失败", e);
        }
    }

    @Override
    public int updateCourse(Course course) {
        String sql = "UPDATE Courses SET CourseName=?, TeacherID=?, TeacherName=?, Department=?, Credit=?, " +
                "Schedule=?, Location=?, Capacity=?, SelectedNum=?, StartWeek=?, EndWeek=? " +
                "WHERE CourseID=?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getTeacherId());
            pstmt.setString(3, course.getTeacherName());
            pstmt.setString(4, course.getDepartment());
            pstmt.setInt(5, course.getCredit());
            pstmt.setString(6, course.getSchedule());
            pstmt.setString(7, course.getLocation());
            pstmt.setInt(8, course.getCapacity());
            pstmt.setInt(9, course.getSelectedNum());
            pstmt.setInt(10, course.getStartWeek());
            pstmt.setInt(11, course.getEndWeek());
            pstmt.setString(12, course.getCourseId());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新课程失败", e);
        }
    }

    @Override
    public int deleteCourse(String courseId) {
        String sql = "DELETE FROM Courses WHERE CourseID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除课程失败", e);
        }
    }

    @Override
    public int selectCourse(String studentId, String courseId) {
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false);

            // 1. 查询学生信息（从tblUser和tblStudent表）
            String studentSql = "SELECT u.tname AS StudentName, s.major AS Department " +
                    "FROM tblUser u " +
                    "JOIN tblStudent s ON u.cid = s.cid " +
                    "WHERE u.cid = ?";
            String studentName = null;
            String department = null;
            try (PreparedStatement pstmt = conn.prepareStatement(studentSql)) {
                pstmt.setString(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        studentName = rs.getString("StudentName");
                        department = rs.getString("Department");
                        System.out.println("找到学生: " + studentId + ", 姓名: " + studentName + ", 专业: " + department);
                    } else {
                        throw new SQLException("学生不存在");
                    }
                }
            }

            // 2. 查询课程信息
            String courseSql = "SELECT CourseName FROM Courses WHERE CourseID = ?";
            String courseName = null;
            try (PreparedStatement pstmt = conn.prepareStatement(courseSql)) {
                pstmt.setString(1, courseId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        courseName = rs.getString("CourseName");
                        System.out.println("找到课程: " + courseId + ", 名称: " + courseName);
                    } else {
                        throw new SQLException("课程不存在");
                    }
                }
            }

            // 3. 检查是否已选该课程
            String checkSql = "SELECT COUNT(*) FROM SelectionRecords WHERE StudentID = ? AND CourseID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setString(1, studentId);
                pstmt.setString(2, courseId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new SQLException("已选过该课程");
                    }
                }
            }

            // 4. 检查课程容量
            String capacitySql = "SELECT Capacity, SelectedNum FROM Courses WHERE CourseID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(capacitySql)) {
                pstmt.setString(1, courseId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int capacity = rs.getInt("Capacity");
                        int selectedNum = rs.getInt("SelectedNum");
                        if (selectedNum >= capacity) {
                            throw new SQLException("课程已满");
                        }
                    }
                }
            }

            // 5. 插入选课记录
            String insertSql = "INSERT INTO SelectionRecords (StudentID, StudentName, CourseID, CourseName, SelectionTime, Department) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, studentId);
                pstmt.setString(2, studentName);
                pstmt.setString(3, courseId);
                pstmt.setString(4, courseName);
                pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                pstmt.setString(6, department);
                int insertResult = pstmt.executeUpdate();
                System.out.println("插入选课记录结果: " + insertResult);
            }

            // 6. 更新课程已选人数
            String updateSql = "UPDATE Courses SET SelectedNum = SelectedNum + 1 WHERE CourseID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setString(1, courseId);
                int updateResult = pstmt.executeUpdate();
                System.out.println("更新已选人数结果: " + updateResult);
            }

            conn.commit();
            return 1;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("选课失败: " + e.getMessage());
            throw new RuntimeException("选课失败: " + e.getMessage(), e);
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
    public List<Course> getCoursesByStudentId(String studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.* FROM Courses c " +
                "JOIN SelectionRecords sr ON c.CourseID = sr.CourseID " +
                "WHERE sr.StudentID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapRowToCourse(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("按学生ID查询课程失败: " + e.getMessage());
            throw new RuntimeException("按学生查询课程失败: " + e.getMessage(), e);
        }
        return courses;
    }

    @Override
    public int dropCourse(String studentId, String courseId) {
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false);

            // 1. 删除选课记录
            String deleteSql = "DELETE FROM SelectionRecords WHERE StudentID = ? AND CourseID = ?";
            int result = 0;

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setString(1, studentId);
                deleteStmt.setString(2, courseId);
                result = deleteStmt.executeUpdate();
            }

            // 2. 如果删除成功，更新课程已选人数
            if (result > 0) {
                String updateSql = "UPDATE Courses SET SelectedNum = SelectedNum - 1 WHERE CourseID = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, courseId);
                    updateStmt.executeUpdate();
                }
            }

            conn.commit();
            return result;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("退课失败: " + e.getMessage(), e);
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
    public List<Course> getCoursesByTeacherId(String teacherId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Courses WHERE TeacherID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapRowToCourse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("按教师查询课程失败", e);
        }
        return courses;
    }

    @Override
    public Course getCourseById(String courseId) {
        String sql = "SELECT * FROM Courses WHERE CourseId = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("找到课程ID为: " + courseId + " 的课程");
                    return mapRowToCourse(rs);
                } else {
                    System.out.println("未找到课程ID为: " + courseId + " 的课程");
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("按ID查询课程失败: " + e.getMessage());
            throw new RuntimeException("按ID查询课程失败", e);
        }
    }

    @Override
    public List<Course> getCoursesByName(String keyword) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Courses WHERE CourseName LIKE ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapRowToCourse(rs));
                }
            }
            return courses;
        } catch (SQLException e) {
            throw new RuntimeException("按名称查询课程失败", e);
        }
    }

    @Override
    public List<SelectionRecord> getSelectionRecords(String courseId) {
        List<SelectionRecord> records = new ArrayList<>();
        String sql = "SELECT sr.StudentID, u.tname AS StudentName, sr.CourseID, c.CourseName, " +
                "sr.SelectionTime, s.major AS Department " +
                "FROM SelectionRecords sr " +
                "JOIN tblUser u ON sr.StudentID = u.cid " +
                "JOIN tblStudent s ON sr.StudentID = s.cid " +
                "JOIN Courses c ON sr.CourseID = c.CourseID " +
                "WHERE sr.CourseID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SelectionRecord record = new SelectionRecord();
                record.setStudentId(rs.getString("StudentID"));
                record.setStudentName(rs.getString("StudentName"));
                record.setCourseId(rs.getString("CourseID"));
                record.setCourseName(rs.getString("CourseName"));

                Timestamp timestamp = rs.getTimestamp("SelectionTime");
                if (timestamp != null) {
                    record.setSelectionTime(timestamp.toLocalDateTime());
                }

                record.setDepartment(rs.getString("Department"));
                records.add(record);
            }

            System.out.println("成功获取课程 " + courseId + " 的 " + records.size() + " 条选课记录");
        } catch (SQLException e) {
            System.err.println("查询选课记录失败: " + e.getMessage());
            throw new RuntimeException("查询选课记录失败", e);
        }

        return records;
    }

    @Override
    public List<Course> getCourseSchedule(String studentId, String semester) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.* FROM Courses c " +
                "JOIN SelectionRecords sr ON c.CourseID = sr.CourseID " +
                "WHERE sr.StudentID = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = mapRowToCourse(rs);
                    courses.add(course);
                }
            }
            System.out.println("成功获取学生 " + studentId + " 的 " + courses.size() + " 门课程");
        } catch (SQLException e) {
            System.err.println("获取课表失败: " + e.getMessage());
            throw new RuntimeException("获取课表失败", e);
        }
        return courses;
    }

    // 获取教师信息（用于课程管理）
    public String getTeacherNameById(String teacherId) {
        String sql = "SELECT tname FROM tblUser WHERE cid = ? AND role = 'TC'";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, teacherId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tname");
                }
            }
        } catch (SQLException e) {
            System.err.println("获取教师姓名失败: " + e.getMessage());
        }
        return "未知教师";
    }

    // 检查学生是否存在
    public boolean studentExists(String studentId) {
        String sql = "SELECT COUNT(*) FROM tblUser WHERE cid = ? AND role = 'ST'";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("检查学生存在失败: " + e.getMessage());
        }
        return false;
    }

    private Course mapRowToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getString("courseId"));
        course.setCourseName(rs.getString("courseName"));
        course.setTeacherName(rs.getString("teacherName"));
        course.setTeacherId(rs.getString("teacherId"));
        course.setDepartment(rs.getString("department"));
        course.setCredit(rs.getInt("credit"));
        course.setSchedule(rs.getString("schedule"));
        course.setLocation(rs.getString("location"));
        course.setCapacity(rs.getInt("capacity"));
        course.setSelectedNum(rs.getInt("selectedNum"));
        course.setStartWeek(rs.getInt("StartWeek"));
        course.setEndWeek(rs.getInt("endweek"));
        return course;
    }
}