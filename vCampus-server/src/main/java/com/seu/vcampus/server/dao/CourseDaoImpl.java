package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Course;
import com.seu.vcampus.common.model.CourseSelectionRule;
import com.seu.vcampus.common.model.SelectionRecord;
import com.seu.vcampus.server.dao.CourseDao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CourseDaoImpl implements CourseDao {
    private static final String DB_PATH = "D:/DataBase/course.accdb";
    private static final String URL = "jdbc:ucanaccess://" + DB_PATH;

    static {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            System.out.println("UCanAccess驱动加载成功");
        } catch (ClassNotFoundException e) {
            System.err.println("UCanAccess驱动加载失败，请检查依赖配置");
            e.printStackTrace();
            throw new RuntimeException("UCanAccess驱动加载失败", e);
        }
    }

    // 获取数据库连接
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // 测试数据库连接
    public static boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            System.out.println("Access数据库连接成功");
            return true;
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            return false;
        }
    }

    // 初始化数据库
    public static void initializeDatabase() {
        System.out.println("开始初始化数据库...");

        try (Connection conn = DriverManager.getConnection(URL);
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
        String sql = "SELECT CourseID, CourseName, TeacherID, Department, Credit, Schedule, Location, Capacity, SelectedNum, StartWeek, EndWeek FROM Courses";

        try (Connection conn = getConnection();
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
    public int selectCourse(String studentId, String courseId) {
        String sql = "INSERT INTO CourseSelections (StudentID, CourseID, SelectionTime) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

            int result = pstmt.executeUpdate();

            // 更新课程已选人数
            if (result > 0) {
                updateSelectedNum(conn, courseId, 1);
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("选课失败", e);
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
    public int dropCourse(String studentId, String courseId) {
        String sql = "DELETE FROM CourseSelections WHERE StudentID = ? AND CourseID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);

            int result = pstmt.executeUpdate();

            // 更新课程已选人数
            if (result > 0) {
                updateSelectedNum(conn, courseId, -1);
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("退课失败", e);
        }
    }

    @Override
    public List<Course> getCoursesByStudentId(String studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.* FROM Courses c " +
                "JOIN CourseSelections cs ON c.CourseID = cs.CourseID " +
                "WHERE cs.StudentID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapRowToCourse(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("按学生查询课程失败", e);
        }
        return courses;
    }

    @Override
    public List<Course> getCoursesByTeacherId(String teacherId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM Courses WHERE TeacherID = ?";

        try (Connection conn = getConnection();
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
    public int updateCourse(Course course) {
        String sql = "UPDATE Courses SET CourseName=?, TeacherID=?, Department=?, Credit=?, Schedule=?, Location=?, Capacity=?, SelectedNum=?, StartWeek=?, EndWeek=? " +
                "WHERE CourseID=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getTeacherId());
            pstmt.setString(3, course.getDepartment());
            pstmt.setInt(4, course.getCredit());
            pstmt.setString(5, course.getTime());
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
    public int addCourse(Course course) {
        String sql = "INSERT INTO Courses (CourseID, CourseName, TeacherID, Department, Credit, Schedule, Location, Capacity, SelectedNum, StartWeek, EndWeek) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, course.getCourseId());
            pstmt.setString(2, course.getCourseName());
            pstmt.setString(3, course.getTeacherId());
            pstmt.setString(4, course.getDepartment());
            pstmt.setInt(5, course.getCredit());
            pstmt.setString(6, course.getTime());
            pstmt.setString(7, course.getLocation());
            pstmt.setInt(8, course.getCapacity());
            pstmt.setInt(9, course.getSelectedNum());
            pstmt.setInt(10, course.getStartWeek());
            pstmt.setInt(11, course.getEndWeek());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("添加课程失败", e);
        }
    }

    @Override
    public int deleteCourse(String courseId) {
        String sql = "DELETE FROM Courses WHERE CourseID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除课程失败", e);
        }
    }

    @Override
    public Course getCourseById(String courseId) {
        String sql = "SELECT * FROM Courses WHERE CourseID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCourse(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询课程失败", e);
        }
        return null;
    }

    @Override
    public List<SelectionRecord> getSelectionRecords(String courseId) {
        List<SelectionRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM CourseSelections WHERE CourseID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, courseId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SelectionRecord record = new SelectionRecord();
                    record.setStudentId(rs.getString("StudentID"));
                    record.setCourseId(rs.getString("CourseID"));
                    record.setSelectionTime(rs.getTimestamp("SelectionTime").toLocalDateTime());
                    records.add(record);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("获取选课记录失败", e);
        }
        return records;
    }


    private Course mapRowToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getString("CourseID"));
        course.setCourseName(rs.getString("CourseName"));
        course.setTeacherId(rs.getString("TeacherID"));
        course.setDepartment(rs.getString("Department"));
        course.setCredit(rs.getInt("Credit"));
        course.setTime(rs.getString("Schedule"));
        course.setLocation(rs.getString("Location"));
        course.setCapacity(rs.getInt("Capacity"));
        course.setSelectedNum(rs.getInt("SelectedNum"));
        course.setStartWeek(rs.getInt("StartWeek"));
        course.setEndWeek(rs.getInt("EndWeek"));
        return course;
    }
}