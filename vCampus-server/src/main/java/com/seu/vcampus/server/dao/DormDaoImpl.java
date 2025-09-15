package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Dorm;
import com.seu.vcampus.common.util.DBConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DormDaoImpl implements DormDao {
    // 住宿信息相关操作实现
    
    @Override
    public Dorm getDormInfoByStudentId(String studentId) throws SQLException {
        String sql = "SELECT * FROM tblDormInfo WHERE studentId = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Dorm dorm = new Dorm();
                    dorm.setStudentId(rs.getString("studentId"));
                    dorm.setName(rs.getString("name"));
                    dorm.setBuilding(rs.getString("building"));
                    dorm.setRoomNumber(rs.getString("roomNumber"));
                    dorm.setBedNumber(rs.getString("bedNumber"));
                    
                    // 处理日期字段
                    java.sql.Date checkInDate = rs.getDate("checkInDate");
                    if (checkInDate != null) {
                        dorm.setCheckInDate(new java.util.Date(checkInDate.getTime()));
                    }
                    
                    dorm.setStatus(rs.getString("status"));
                    return dorm;
                }
            }
        } catch (SQLException ex) {
            System.err.println("获取住宿信息失败: " + ex.getMessage());
            throw ex;
        }
        return null;
    }

    @Override
    public List<Dorm> getAllDormInfo() throws SQLException {
        List<Dorm> dorms = new ArrayList<>();
        String sql = "SELECT * FROM tblDormInfo";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Dorm dorm = new Dorm();
                dorm.setStudentId(rs.getString("studentId"));
                dorm.setName(rs.getString("name"));
                dorm.setBuilding(rs.getString("building"));
                dorm.setRoomNumber(rs.getString("roomNumber"));
                dorm.setBedNumber(rs.getString("bedNumber"));
                
                // 处理日期字段
                java.sql.Date checkInDate = rs.getDate("checkInDate");
                if (checkInDate != null) {
                    dorm.setCheckInDate(new java.util.Date(checkInDate.getTime()));
                }
                
                dorm.setStatus(rs.getString("status"));
                dorms.add(dorm);
            }
        } catch (SQLException ex) {
            System.err.println("获取所有住宿信息失败: " + ex.getMessage());
            throw ex;
        }
        return dorms;
    }

    @Override
    public boolean updateDormInfo(Dorm dorm) throws SQLException {
        String sql = "UPDATE tblDormInfo SET name = ?, building = ?, roomNumber = ?, " +
                     "bedNumber = ?, checkInDate = ?, status = ? WHERE studentId = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dorm.getName());
            ps.setString(2, dorm.getBuilding());
            ps.setString(3, dorm.getRoomNumber());
            ps.setString(4, dorm.getBedNumber());
            
            // 处理日期字段
            if (dorm.getCheckInDate() != null) {
                ps.setDate(5, new java.sql.Date(dorm.getCheckInDate().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            
            ps.setString(6, dorm.getStatus());
            ps.setString(7, dorm.getStudentId());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            System.err.println("更新住宿信息失败: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public boolean addDormInfo(Dorm dorm) throws SQLException {
        String sql = "INSERT INTO tblDormInfo (studentId, name, building, roomNumber, " +
                     "bedNumber, checkInDate, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dorm.getStudentId());
            ps.setString(2, dorm.getName());
            ps.setString(3, dorm.getBuilding());
            ps.setString(4, dorm.getRoomNumber());
            ps.setString(5, dorm.getBedNumber());
            
            // 处理日期字段
            if (dorm.getCheckInDate() != null) {
                ps.setDate(6, new java.sql.Date(dorm.getCheckInDate().getTime()));
            } else {
                ps.setNull(6, Types.DATE);
            }
            
            ps.setString(7, dorm.getStatus());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            System.err.println("添加住宿信息失败: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public boolean deleteDormInfo(String studentId) throws SQLException {
        String sql = "DELETE FROM tblDormInfo WHERE studentId = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            System.err.println("删除住宿信息失败: " + ex.getMessage());
            throw ex;
        }
    }
    
    // 住宿申请相关操作实现
    
    @Override
    public boolean addApplication(Dorm application) throws SQLException {
        String sql = "INSERT INTO tblDormApplication (studentId, applicationType, " +
                     "applicationTime, applicationStatus, reviewer) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, application.getStudentId());
            ps.setString(2, application.getApplicationType());
            
            // 处理时间字段
            if (application.getApplicationTime() != null) {
                ps.setTimestamp(3, new Timestamp(application.getApplicationTime().getTime()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            
            ps.setString(4, application.getApplicationStatus());
            ps.setString(5, application.getReviewer());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            System.err.println("添加申请失败: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<Dorm> getApplicationsByStudentId(String studentId) throws SQLException {
        List<Dorm> applications = new ArrayList<>();
        String sql = "SELECT * FROM tblDormApplication WHERE studentId = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Dorm application = new Dorm();
                    application.setApplicationId(rs.getString("applicationId"));
                    application.setStudentId(rs.getString("studentId"));
                    application.setApplicationType(rs.getString("applicationType"));
                    
                    // 处理时间字段
                    Timestamp applicationTime = rs.getTimestamp("applicationTime");
                    if (applicationTime != null) {
                        application.setApplicationTime(new java.util.Date(applicationTime.getTime()));
                    }
                    
                    application.setApplicationStatus(rs.getString("applicationStatus"));
                    application.setReviewer(rs.getString("reviewer"));
                    applications.add(application);
                }
            }
        } catch (SQLException ex) {
            System.err.println("获取申请记录失败: " + ex.getMessage());
            throw ex;
        }
        return applications;
    }

    @Override
    public List<Dorm> getPendingApplications() throws SQLException {
        List<Dorm> applications = new ArrayList<>();
        String sql = "SELECT * FROM tblDormApplication WHERE applicationStatus = '待审核'";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Dorm application = new Dorm();
                application.setApplicationId(rs.getString("applicationId"));
                application.setStudentId(rs.getString("studentId"));
                application.setApplicationType(rs.getString("applicationType"));
                
                // 处理时间字段
                Timestamp applicationTime = rs.getTimestamp("applicationTime");
                if (applicationTime != null) {
                    application.setApplicationTime(new java.util.Date(applicationTime.getTime()));
                }
                
                application.setApplicationStatus(rs.getString("applicationStatus"));
                application.setReviewer(rs.getString("reviewer"));
                applications.add(application);
            }
        } catch (SQLException ex) {
            System.err.println("获取待审核申请失败: " + ex.getMessage());
            throw ex;
        }
        return applications;
    }

    @Override
    public boolean updateApplicationStatus(String applicationId, String status, String reviewer) throws SQLException {
        String sql = "UPDATE tblDormApplication SET applicationStatus = ?, reviewer = ? WHERE applicationId = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, reviewer);
            ps.setString(3, applicationId);
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            System.err.println("更新申请状态失败: " + ex.getMessage());
            throw ex;
        }
    }
    
    // 宿舍服务相关操作实现
    
    @Override
    public boolean addService(Dorm service) throws SQLException {
        String sql = "INSERT INTO tblDormService (studentId, serviceDescription, " +
                     "serviceTime, serviceStatus, serviceProcessor) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, service.getStudentId());
            ps.setString(2, service.getServiceDescription());
            
            // 处理时间字段
            if (service.getServiceTime() != null) {
                ps.setTimestamp(3, new Timestamp(service.getServiceTime().getTime()));
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            
            ps.setString(4, service.getServiceStatus());
            ps.setString(5, service.getServiceProcessor());
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            System.err.println("添加服务失败: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<Dorm> getServicesByStudentId(String studentId) throws SQLException {
        List<Dorm> services = new ArrayList<>();
        String sql = "SELECT * FROM tblDormService WHERE studentId = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Dorm service = new Dorm();
                    service.setServiceId(rs.getString("serviceId"));
                    service.setStudentId(rs.getString("studentId"));
                    service.setServiceDescription(rs.getString("serviceDescription"));
                    
                    // 处理时间字段
                    Timestamp serviceTime = rs.getTimestamp("serviceTime");
                    if (serviceTime != null) {
                        service.setServiceTime(new java.util.Date(serviceTime.getTime()));
                    }
                    
                    service.setServiceStatus(rs.getString("serviceStatus"));
                    service.setServiceProcessor(rs.getString("serviceProcessor"));
                    services.add(service);
                }
            }
        } catch (SQLException ex) {
            System.err.println("获取服务记录失败: " + ex.getMessage());
            throw ex;
        }
        return services;
    }

    @Override
    public List<Dorm> getAllServices() throws SQLException {
        List<Dorm> services = new ArrayList<>();
        String sql = "SELECT * FROM tblDormService";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Dorm service = new Dorm();
                service.setServiceId(rs.getString("serviceId"));
                service.setStudentId(rs.getString("studentId"));
                service.setServiceDescription(rs.getString("serviceDescription"));
                
                // 处理时间字段
                Timestamp serviceTime = rs.getTimestamp("serviceTime");
                if (serviceTime != null) {
                    service.setServiceTime(new java.util.Date(serviceTime.getTime()));
                }
                
                service.setServiceStatus(rs.getString("serviceStatus"));
                service.setServiceProcessor(rs.getString("serviceProcessor"));
                services.add(service);
            }
        } catch (SQLException ex) {
            System.err.println("获取所有服务记录失败: " + ex.getMessage());
            throw ex;
        }
        return services;
    }

    @Override
    public boolean updateServiceStatus(String serviceId, String status, String processor) throws SQLException {
        String sql = "UPDATE tblDormService SET serviceStatus = ?, serviceProcessor = ? WHERE serviceId = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, processor);
            ps.setString(3, serviceId);
            
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            System.err.println("更新服务状态失败: " + ex.getMessage());
            throw ex;
        }
    }
}
