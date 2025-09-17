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
        if (filters != null && !filters.isEmpty()) {
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
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("role"),
                            rs.getString("sex"),
                            rs.getString("birthday"),
                            rs.getString("address"),
                            rs.getString("nid"),
                            rs.getString("endate"),
                            rs.getString("grade"),
                            rs.getString("major"),
                            rs.getString("stid"),
                            rs.getString("es"),
                            rs.getString("esState")

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
                        rs.getString("esState")
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
