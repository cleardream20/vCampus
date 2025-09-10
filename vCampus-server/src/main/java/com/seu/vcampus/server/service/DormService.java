package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.Dorm;
import java.util.List;

public interface DormService {
    // 基本住宿服务
    Dorm getDormInfo(String studentId);
    List<Dorm> getAllDorms();
    boolean updateDorm(Dorm dorm);
    boolean createDorm(Dorm dorm);
    boolean deleteDorm(Long dormId); // 添加缺失的方法
    boolean hasAvailableBed(Long dormId); // 添加缺失的方法
    List<Dorm> searchDorms(String keyword); // 添加缺失的方法
    boolean updateDorm(Long dormId, Dorm dorm); // 添加缺失的方法
    
    // 申请相关服务
    boolean submitApplication(Dorm application);
    List<Dorm> getStudentApplications(String studentId);
    List<Dorm> getPendingApplications();
    boolean approveApplication(String studentId, String reviewer, String remarks);
    boolean rejectApplication(String studentId, String reviewer, String remarks);
    
    // 服务请求相关服务
    boolean submitServiceRequest(Dorm serviceRequest);
    List<Dorm> getStudentServiceRequests(String studentId);
    List<Dorm> getAllServiceRequests();
    boolean processServiceRequest(String studentId, String processor);
}
