package com.seu.vcampus.server.service;

import com.seu.vcampus.common.model.Dorm;
import com.seu.vcampus.server.dao.DormDaoImpl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class DormServiceImpl implements IDormService {
    private final DormDaoImpl dormDao = new DormDaoImpl();

    // 学生端功能实现
    
    @Override
    public Dorm getDormInfo(String studentId) throws SQLException {
        try {
            return dormDao.getDormInfoByStudentId(studentId);
        } catch (SQLException e) {
            System.err.println("获取住宿信息失败: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Dorm> getApplications(String studentId) throws SQLException {
        try {
            return dormDao.getApplicationsByStudentId(studentId);
        } catch (SQLException e) {
            System.err.println("获取申请记录失败: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean submitApplication(Dorm application) throws SQLException {
        try {
            // 设置默认值
            application.setApplicationTime(new Date());
            application.setApplicationStatus("待审核");
            
            return dormDao.addApplication(application);
        } catch (SQLException e) {
            System.err.println("提交申请失败: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Dorm> getServices(String studentId) throws SQLException {
        try {
            return dormDao.getServicesByStudentId(studentId);
        } catch (SQLException e) {
            System.err.println("获取服务记录失败: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean submitService(Dorm service) throws SQLException {
        try {
            // 设置默认值
            service.setServiceTime(new Date());
            service.setServiceStatus("待处理");
            
            return dormDao.addService(service);
        } catch (SQLException e) {
            System.err.println("提交服务申请失败: " + e.getMessage());
            throw e;
        }
    }

    // 管理端功能实现
    
    @Override
    public List<Dorm> getAllDormInfo() throws SQLException {
        try {
            return dormDao.getAllDormInfo();
        } catch (SQLException e) {
            System.err.println("获取所有住宿信息失败: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Dorm> getPendingApplications() throws SQLException {
        try {
            return dormDao.getPendingApplications();
        } catch (SQLException e) {
            System.err.println("获取待审核申请失败: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateApplicationStatus(String applicationId, String status, String reviewer) throws SQLException {
        try {
            // 验证状态值
            if (!"已批准".equals(status) && !"已拒绝".equals(status)) {
                throw new SQLException("无效的申请状态: " + status);
            }
            
            return dormDao.updateApplicationStatus(applicationId, status, reviewer);
        } catch (SQLException e) {
            System.err.println("更新申请状态失败: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Dorm> getAllServices() throws SQLException {
        try {
            return dormDao.getAllServices();
        } catch (SQLException e) {
            System.err.println("获取所有服务记录失败: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateServiceStatus(String serviceId, String status, String processor) throws SQLException {
        try {
            // 验证状态值
            if (!"处理中".equals(status) && !"已完成".equals(status)) {
                throw new SQLException("无效的服务状态: " + status);
            }
            
            return dormDao.updateServiceStatus(serviceId, status, processor);
        } catch (SQLException e) {
            System.err.println("更新服务状态失败: " + e.getMessage());
            throw e;
        }
    }
}

