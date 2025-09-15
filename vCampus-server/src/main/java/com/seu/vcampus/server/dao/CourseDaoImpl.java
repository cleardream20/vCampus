package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.course.*;
import com.seu.vcampus.common.util.DBConnector;
import com.seu.vcampus.server.dao.CourseDao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CourseDaoImpl implements CourseDao {
    // 初始化数据库
    public static void initializeDatabase() {
        System.out.println("开始初始化数据库...");

        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            // 创建Courses表
            String createTableSQL = "CREATE TABLE IF NOT EXISTS Courses (" +
                    "CourseID VARCHAR(10) PRIMARY KEY, " +
                    "CourseName VARCHAR(50) NOT NULL, " +
                    "TeacherID VARCHAR(10) NOT NULL, " +
                    "Department VARCHAR(30) NOT NULL, " +
                    "Credit INT NOT NULL, " +
                    "Schedule VARCHAR(50) NOT NULL, " +
                    "Location VARCHAR(50) NOT NULL, " +
                    "Capacity INT NOT NULL, " +
                    "SelectedNum INT DEFAULT 0, " +
                    "StartWeek INT NOT NULL, " +
                    "EndWeek INT NOT NULL)";

            stmt.execute(createTableSQL);
            System.out.println("Courses表创建成功");

            // 创建CourseSelections表
            String createSelectionsSQL = "CREATE TABLE IF NOT EXISTS CourseSelections (" +
                    "SelectionID AUTOINCREMENT PRIMARY KEY, " +
                    "StudentID VARCHAR(10) NOT NULL, " +
                    "CourseID VARCHAR(10) NOT NULL, " +
                    "SelectionTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

            stmt.execute(createSelectionsSQL);
            System.out.println("CourseSelections表创建成功");

            // 添加示例数据
            String insertDataSQL = "INSERT INTO Courses (CourseID, CourseName, TeacherID, Department, Credit, Schedule, Location, Capacity, SelectedNum, StartWeek, EndWeek) VALUES " +
                    "('CS101', '计算机科学导论', 'T001', '计算机学院', 3, '周一8:00-9:40', '逸夫楼201', 100, 85, 1, 16), " +
                    "('MA201', '高等数学', 'T002', '数学系', 4, '周二10:00-11:40', '数学楼101', 150, 142, 1, 16)";

            stmt.executeUpdate(insertDataSQL);
            System.out.println("示例数据插入成功");

        } catch (SQLException e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            throw new RuntimeException("数据库初始化失败", e);
        }
    }


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
        // 使用更简单的SQL语句，避免复杂索引
        String sql = "INSERT INTO Courses (CourseID, CourseName, TeacherID, TeacherName, Department, Credit, Schedule, Location, Capacity, SelectedNum, StartWeek, EndWeek) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置参数
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
        String sql = "UPDATE Courses SET CourseName=?, TeacherID=?, Department=?, Credit=?, " +
                "Schedule=?, Location=?, Capacity=?, SelectedNum=?, StartWeek=?, EndWeek=? " +
                "WHERE CourseID=?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getTeacherId());
            pstmt.setString(3, course.getDepartment());
            pstmt.setInt(4, course.getCredit());
            pstmt.setString(5, course.getSchedule());
            pstmt.setString(6, course.getLocation());
            pstmt.setInt(7, course.getCapacity());
            pstmt.setInt(8, course.getSelectedNum());
            pstmt.setInt(9, course.getStartWeek());
            pstmt.setInt(10, course.getEndWeek());
            pstmt.setString(11, course.getCourseId());

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
            conn.setAutoCommit(false); // 开始事务

            // 1. 查询学生信息（从Users表）
            String studentSql = "SELECT Name, Department FROM Users WHERE ID = ?";
            String studentName = null;
            String department = null;
            try (PreparedStatement pstmt = conn.prepareStatement(studentSql)) {
                pstmt.setString(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        studentName = rs.getString("Name");
                        department = rs.getString("Department");
                        System.out.println("找到学生: " + studentId + ", 姓名: " + studentName + ", 院系: " + department);
                    } else {
                        throw new SQLException("学生不存在");
                    }
                }
            }

            // 2. 查询课程信息（从Courses表）
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

            // 3. 插入选课记录（到SelectionRecords表）
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

            // 4. 更新课程已选人数（在Courses表中）
            String updateSql = "UPDATE Courses SET SelectedNum = SelectedNum + 1 WHERE CourseID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setString(1, courseId);
                int updateResult = pstmt.executeUpdate();
                System.out.println("更新已选人数结果: " + updateResult);
            }

            conn.commit(); // 提交事务
            return 1;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // 回滚事务
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("选课失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("选课失败", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // 恢复自动提交
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
            // 添加更详细的错误信息
            System.err.println("按学生ID查询课程失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("按学生查询课程失败: " + e.getMessage(), e);
        }
        return courses;
    }

    @Override
    public int dropCourse(String studentId, String courseId) {
        Connection conn = null;
        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // 开始事务

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
                    int updateResult = updateStmt.executeUpdate();

                    if (updateResult == 0) {
                        // 课程不存在或更新失败
                        conn.rollback(); // 回滚事务
                        return 0;
                    }
                }
            }

            conn.commit(); // 提交事务
            return result;
        } catch (SQLException e) {
            // 发生异常时回滚事务
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("退课失败: " + e.getMessage(), e);
        } finally {
            // 恢复自动提交并关闭连接
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

    private void updateSelectedNum(Connection conn, String courseId, int delta) throws SQLException {
        String sql = "UPDATE Courses SET SelectedNum = SelectedNum + ? WHERE CourseID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, delta);
            pstmt.setString(2, courseId);
            pstmt.executeUpdate();
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
                    // 添加日志记录未找到课程
                    System.out.println("未找到课程ID为: " + courseId + " 的课程");
                    return null;
                }
            }
        } catch (SQLException e) {
            // 添加更详细的错误日志
            System.err.println("按ID查询课程失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("按ID查询课程失败", e);
        }
    }

    @Override
    public List<Course> getCoursesByName(String keyword) {
        List<Course> courses = new ArrayList<>();
        // 使用LIKE进行模糊查询，%表示任意字符
        String sql = "SELECT * FROM Courses WHERE CourseName LIKE ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 添加通配符进行模糊匹配
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
        String sql = "SELECT sr.StudentID, u.Name AS StudentName, sr.CourseID, c.CourseName, " +
                "sr.SelectionTime, u.Department " +
                "FROM SelectionRecords sr " +
                "JOIN Users u ON sr.StudentID = u.ID " +
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

                // 关键修复：将Timestamp转为LocalDateTime
                Timestamp timestamp = rs.getTimestamp("SelectionTime");
                if (timestamp != null) {
                    record.setSelectionTime(timestamp.toLocalDateTime());
                } else {
                    record.setSelectionTime(null); // 或设置默认值
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
                "WHERE sr.StudentID = ? AND c.semester = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, semester);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = mapRowToCourse(rs);
                    courses.add(course);
                }
            }
            System.out.println("成功获取学生 " + studentId + " 在学期 " + semester + " 的 " + courses.size() + " 门课程");
        } catch (SQLException e) {
            System.err.println("获取课表失败: " + e.getMessage());
            throw new RuntimeException("获取课表失败", e);
        }
        return courses;
    }

    private Course mapRowToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getString("courseId"));
        course.setCourseName(rs.getString("courseName"));
        course.setTeacherName(rs.getString("teacherName")); // 新增
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