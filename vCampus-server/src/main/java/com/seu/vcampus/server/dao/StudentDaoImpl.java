package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Student;
import com.seu.vcampus.common.util.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StudentDaoImpl implements StudentDao {
    @Override
    public List<Student> getStudents(HashMap<Integer, String> filters) throws SQLException {
        List<Student> students = new ArrayList<>();
        //一卡通号、身份证号、学号、姓名、性别、电话号码、出生日期、家庭住址、入学日期、学籍号、学院、年级、学制、学籍状态
        String[] args = new String[] {"cid", "nid", "tsid", "name", "sex", "phone", "birthday", "address", "endate", "stid", "major", "grade", "es", "esState"};
        String sql = "select tu.cid as cid, tu.*, ts.* from tblStudent ts " +
                     "inner join tblUser tu on ts.cid = tu.cid " +
                     "where 1 = 1 ";
        if(filters != null && !filters.isEmpty()){
            for(HashMap.Entry<Integer, String> entry : filters.entrySet()){
                String filterText = entry.getValue().trim();
                if (!filterText.isEmpty()) {
                    sql += "and " + args[entry.getKey()] + " like '%" + filterText + "%'";
                }
            }
        }
        try (Connection conn = DBConnector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student(
                            rs.getString("cid"),
                            rs.getString("password"),
                            rs.getString("tsid"),
                            rs.getString("tname"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("role"),
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
                    students.add(student);
                }
            } catch (SQLException se) {
                se.printStackTrace();
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return students;
    }

    @Override
    public boolean addStudent(List<Student> students) throws SQLException {

        Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnector.getConnection(); // 根据您的实际数据源调整
            conn.setAutoCommit(false);

            // 查询空学生记录
            String selectSql = "select top " + students.size() + " cid from tblStudent where nid is null order by cid ";
            selectStmt = conn.prepareStatement(selectSql);
            rs = selectStmt.executeQuery();

            // 收集可用的CID
            List<String> availableCids = new ArrayList<>();
            while (rs.next()) {
                availableCids.add(rs.getString("cid"));
            }

            // 检查是否获取到足够的空记录
            if (availableCids.size() < students.size()) {
                conn.rollback();
                return false;
            }

            // 准备更新语句
            String updateSql = "update tblStudent set gender = ? and birthday = ? and address = ? and nid = ? and endate = ? and grade = ? and major = ? and stid = ? and es = ? and esState = ? where cid = ?";
            updateStmt = conn.prepareStatement(updateSql);

            // 遍历学生列表进行更新
            for (int i = 0; i < students.size(); i++) {
                Student student = students.get(i);
                String cid = availableCids.get(i);

                updateStmt.setString(1, student.getSex());
                updateStmt.setString(2, student.getBirthday());
                updateStmt.setString(3, student.getAddress());
                updateStmt.setString(4, student.getNid());
                updateStmt.setString(5, student.getEndate());
                updateStmt.setString(6, student.getGrade());
                updateStmt.setString(7, student.getMajor());
                updateStmt.setString(8, student.getStid());
                updateStmt.setString(9, student.getEs());
                updateStmt.setString(10, student.getEsState());
                updateStmt.setString(11, cid);

                updateStmt.addBatch(); // 加入批处理
            }

            // 执行批处理
            int[] updateCounts = updateStmt.executeBatch();

            // 验证所有更新是否成功
            for (int count : updateCounts) {
                if (count != 1) { // 每次更新应影响1行
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Student getStudent(String cid) throws SQLException {
        Student student = null;
        String sql = "select tu.cid as cid, tu.*, ts.* from tblStudent ts " +
                     "inner join tblUser tu on ts.cid = tu.cid " +
                     "where ts.cid = ?";
        try (Connection conn = DBConnector.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    student = new Student(
                            rs.getString("cid"),
                            "-",
                            rs.getString("tsid"),
                            rs.getString("tname"),
                            "-",
                            rs.getString("phone"),
                            "-",
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
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }
}