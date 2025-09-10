package com.seu.vcampus.server.service; 

import com.seu.vcampus.server.dao.DormDao;
import com.seu.vcampus.common.model.Dorm;

import java.util.List;

public class DormServiceImpl implements DormService {
    private final DormDao dormDao;

    public DormServiceImpl(DormDao dormDao) {
        this.dormDao = dormDao;
    }

    @Override
    public Dorm getDormInfo(String studentId) {
        return dormDao.getDormByStudentId(studentId);
    }

    @Override
    public List<Dorm> getAllDorms() {
        return dormDao.getAllDorms();
    }

    @Override
    public boolean updateDorm(Dorm dorm) {
        return dormDao.updateDorm(dorm);
    }

    @Override
    public boolean createDorm(Dorm dorm) {
        return dormDao.createDorm(dorm);
    }

    @Override
    public boolean submitApplication(Dorm application) {
        return dormDao.submitApplication(application);
    }

    @Override
    public List<Dorm> getStudentApplications(String studentId) {
        return dormDao.getApplicationsByStudentId(studentId);
    }

    @Override
    public List<Dorm> getPendingApplications() {
        return dormDao.getPendingApplications();
    }

    @Override
    public boolean approveApplication(String studentId, String reviewer, String remarks) {
        return dormDao.updateApplicationStatus(studentId, "已批准", reviewer, remarks);
    }

    @Override
    public boolean rejectApplication(String studentId, String reviewer, String remarks) {
        return dormDao.updateApplicationStatus(studentId, "已拒绝", reviewer, remarks);
    }

    @Override
    public boolean submitServiceRequest(Dorm serviceRequest) {
        return dormDao.submitServiceRequest(serviceRequest);
    }

    @Override
    public List<Dorm> getStudentServiceRequests(String studentId) {
        return dormDao.getServiceRequestsByStudentId(studentId);
    }

    @Override
    public List<Dorm> getAllServiceRequests() {
        return dormDao.getAllServiceRequests();
    }

    @Override
    public boolean processServiceRequest(String studentId, String processor) {
        return dormDao.updateServiceRequestStatus(studentId, "处理中", processor);
    }

    // 添加缺失的方法实现
    @Override
    public boolean deleteDorm(Long dormId) {
        // 实现删除逻辑
        return false;
    }

    @Override
    public boolean hasAvailableBed(Long dormId) {
        // 实现检查床位逻辑
        return false;
    }

    @Override
    public List<Dorm> searchDorms(String keyword) {
        // 实现搜索逻辑
        return null;
    }

    @Override
    public boolean updateDorm(Long dormId, Dorm dorm) {
        // 实现更新逻辑
        return false;
    }
}
