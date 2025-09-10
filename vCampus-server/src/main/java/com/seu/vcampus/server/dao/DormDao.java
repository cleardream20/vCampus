// DormDao.java
package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Dorm;
import java.util.List;

public interface DormDao {
    // 基本住宿信息操作
    Dorm getDormByStudentId(String studentId);
    List<Dorm> getAllDorms();
    boolean updateDorm(Dorm dorm);
    boolean createDorm(Dorm dorm);
    
    // 申请相关操作
    boolean submitApplication(Dorm application);
    List<Dorm> getApplicationsByStudentId(String studentId);
    List<Dorm> getPendingApplications();
    boolean updateApplicationStatus(String studentId, String status, String reviewer, String remarks);
    
    // 服务请求相关操作
    boolean submitServiceRequest(Dorm serviceRequest);
    List<Dorm> getServiceRequestsByStudentId(String studentId);
    List<Dorm> getAllServiceRequests();
    boolean updateServiceRequestStatus(String studentId, String status, String processor);
}
