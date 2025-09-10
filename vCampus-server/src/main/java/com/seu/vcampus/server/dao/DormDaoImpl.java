// DormDaoImpl.java
package com.seu.vcampus.server.dao;

import com.seu.vcampus.common.model.Dorm;
import java.util.*;

public class DormDaoImpl implements DormDao {
    // 模拟数据存储
    private Map<String, Dorm> dormMap = new HashMap<>();
    private Map<String, Dorm> applicationMap = new HashMap<>(); // 存储申请信息
    private Map<String, Dorm> serviceRequestMap = new HashMap<>(); // 存储服务请求

    public DormDaoImpl() {
        // 初始化一些模拟数据
        initializeMockData();
    }

    private void initializeMockData() {
        // 初始化模拟住宿信息
        Dorm dorm1 = new Dorm();
        dorm1.setStudentId("123456789");
        dorm1.setName("张三");
        dorm1.setCollege("计算机科学与技术");
        dorm1.setMajor("软件工程");
        dorm1.setBuilding("紫荆1号楼");
        dorm1.setRoomNumber("101A");
        dorm1.setBedNumber("1");
        dorm1.setDormType("4人间");
        dorm1.setCheckInDate("2023-09-01");
        dorm1.setExpectedCheckOutDate("2027-06-30");
        dorm1.setStatus("在住");
        dorm1.setDormPhone("010-12345678");
        dorm1.setRoommates("李四, 王五, 赵六");
        dorm1.setDormManager("陈老师");
        dorm1.setManagerPhone("010-87654321");
        dormMap.put(dorm1.getStudentId(), dorm1);
        
        // 可以添加更多模拟数据...
    }

    @Override
    public Dorm getDormByStudentId(String studentId) {
        return dormMap.get(studentId);
    }

    @Override
    public List<Dorm> getAllDorms() {
        return new ArrayList<>(dormMap.values());
    }

    @Override
    public boolean updateDorm(Dorm dorm) {
        if (dorm.getStudentId() != null) {
            dormMap.put(dorm.getStudentId(), dorm);
            return true;
        }
        return false;
    }

    @Override
    public boolean createDorm(Dorm dorm) {
        if (dorm.getStudentId() != null) {
            dormMap.put(dorm.getStudentId(), dorm);
            return true;
        }
        return false;
    }

    @Override
    public boolean submitApplication(Dorm application) {
        if (application.getStudentId() != null) {
            application.setApplicationTime(new Date());
            application.setApplicationStatus("待审核");
            applicationMap.put(application.getStudentId(), application);
            return true;
        }
        return false;
    }

    @Override
    public List<Dorm> getApplicationsByStudentId(String studentId) {
        List<Dorm> result = new ArrayList<>();
        Dorm application = applicationMap.get(studentId);
        if (application != null) {
            result.add(application);
        }
        return result;
    }

    @Override
    public List<Dorm> getPendingApplications() {
        List<Dorm> result = new ArrayList<>();
        for (Dorm app : applicationMap.values()) {
            if ("待审核".equals(app.getApplicationStatus())) {
                result.add(app);
            }
        }
        return result;
    }

    @Override
    public boolean updateApplicationStatus(String studentId, String status, String reviewer, String remarks) {
        Dorm app = applicationMap.get(studentId);
        if (app != null) {
            app.setApplicationStatus(status);
            app.setReviewer(reviewer);
            app.setReviewRemarks(remarks);
            return true;
        }
        return false;
    }

    @Override
    public boolean submitServiceRequest(Dorm serviceRequest) {
        if (serviceRequest.getStudentId() != null) {
            serviceRequest.setServiceTime(new Date());
            serviceRequest.setServiceStatus("待处理");
            serviceRequestMap.put(serviceRequest.getStudentId(), serviceRequest);
            return true;
        }
        return false;
    }

    @Override
    public List<Dorm> getServiceRequestsByStudentId(String studentId) {
        List<Dorm> result = new ArrayList<>();
        Dorm serviceRequest = serviceRequestMap.get(studentId);
        if (serviceRequest != null) {
            result.add(serviceRequest);
        }
        return result;
    }

    @Override
    public List<Dorm> getAllServiceRequests() {
        return new ArrayList<>(serviceRequestMap.values());
    }

    @Override
    public boolean updateServiceRequestStatus(String studentId, String status, String processor) {
        Dorm sr = serviceRequestMap.get(studentId);
        if (sr != null) {
            sr.setServiceStatus(status);
            sr.setServiceProcessor(processor);
            if ("处理中".equals(status)) {
                sr.setExpectedCompletionTime(new Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000)); // 3天后
            }
            return true;
        }
        return false;
    }
}
