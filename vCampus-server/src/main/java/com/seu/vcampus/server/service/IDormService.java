package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.Dorm;
import java.sql.SQLException;
import java.util.List;

public interface IDormService {
    // 学生端功能
    Dorm getDormInfo(String studentId) throws SQLException;
    List<Dorm> getApplications(String studentId) throws SQLException;
    boolean submitApplication(Dorm application) throws SQLException;
    List<Dorm> getServices(String studentId) throws SQLException;
    boolean submitService(Dorm service) throws SQLException;
    
    // 管理端功能
    List<Dorm> getAllDormInfo() throws SQLException;
    List<Dorm> getPendingApplications() throws SQLException;
    boolean updateApplicationStatus(String applicationId, String status, String reviewer) throws SQLException;
    List<Dorm> getAllServices() throws SQLException;
    boolean updateServiceStatus(String serviceId, String status, String processor) throws SQLException;
}
