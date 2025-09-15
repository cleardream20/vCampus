package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Dorm;
import java.sql.SQLException;
import java.util.List;

public interface DormDao {
    // 住宿信息相关操作
    Dorm getDormInfoByStudentId(String studentId) throws SQLException;
    List<Dorm> getAllDormInfo() throws SQLException;
    boolean updateDormInfo(Dorm dorm) throws SQLException;
    boolean addDormInfo(Dorm dorm) throws SQLException;
    boolean deleteDormInfo(String studentId) throws SQLException;
    
    // 住宿申请相关操作
    boolean addApplication(Dorm application) throws SQLException;
    List<Dorm> getApplicationsByStudentId(String studentId) throws SQLException;
    List<Dorm> getPendingApplications() throws SQLException;
    boolean updateApplicationStatus(String applicationId, String status, String reviewer) throws SQLException;
    
    // 宿舍服务相关操作
    boolean addService(Dorm service) throws SQLException;
    List<Dorm> getServicesByStudentId(String studentId) throws SQLException;
    List<Dorm> getAllServices() throws SQLException;
    boolean updateServiceStatus(String serviceId, String status, String processor) throws SQLException;
}
