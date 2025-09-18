package com.seu.vcampus.server.controller;

import com.seu.vcampus.common.model.Dorm;
import com.seu.vcampus.common.model.User;
import com.seu.vcampus.common.util.DormMessage;
import com.seu.vcampus.common.util.Jsonable;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.server.service.DormServiceImpl;
import com.seu.vcampus.server.service.IDormService;

import java.sql.SQLException;
import java.util.Map;

public class DormController implements RequestController {
    private final IDormService dormService = new DormServiceImpl();

    @Override
    public Message handleRequest(Message request) {
        Object rawData = request.getData();
        Map<String, Object> dataMap = null;
        User user = null;

        // 提取用户信息
        if (rawData instanceof Map) {
            dataMap = (Map<String, Object>) rawData;
            user = extractUserFromData(dataMap);
        }

        // 根据请求类型处理
        switch (request.getType()) {
            // 学生端功能
            case DormMessage.GET_DORM_INFO:
                return handleGetDormInfo(request, user);
                
            case DormMessage.SUBMIT_APPLICATION:
                return handleSubmitApplication(request, user, dataMap);
                
            case DormMessage.GET_APPLICATIONS:
                return handleGetApplications(request, user);
                
            case DormMessage.SUBMIT_SERVICE:
                return handleSubmitService(request, user, dataMap);
                
            case DormMessage.GET_SERVICES:
                return handleGetServices(request, user);
                
            // 管理端功能
            case DormMessage.GET_ALL_DORM_INFO:
                return handleGetAllDormInfo(request, user);
                
            case DormMessage.GET_PENDING_APPLICATIONS:
                return handleGetPendingApplications(request, user);
                
            case DormMessage.UPDATE_APPLICATION_STATUS:
                return handleUpdateApplicationStatus(request, user, dataMap);
                
            case DormMessage.GET_ALL_SERVICES:
                return handleGetAllServices(request, user);
                
            case DormMessage.UPDATE_SERVICE_STATUS:
                return handleUpdateServiceStatus(request, user, dataMap);
                
            default:
                return createErrorResponse(request, "未知的宿舍管理请求类型: " + request.getType());
        }
    }

    // 从数据中提取用户信息
    private User extractUserFromData(Map<String, Object> dataMap) {
        if (dataMap == null || !dataMap.containsKey("user")) {
            return null;
        }
        Object userObj = dataMap.get("user");
        if (userObj instanceof User) {
            return (User) userObj;
        }
        // 如果是其他类型，转换为JSON字符串再解析
        String userJson = Jsonable.toJson(userObj);
        return Jsonable.fromJson(userJson, User.class);
    }

    // 学生端功能处理方法
    
    private Message handleGetDormInfo(Message request, User user) {
        if (user == null) {
            return createErrorResponse(request, "用户未登录，请先登录");
        }
        
        try {
            Dorm dormInfo = dormService.getDormInfo(user.getCid());
            Message response = new Message(request.getType());
            response.setStatus(Message.STATUS_SUCCESS);
            response.setData(dormInfo);
            return response;
        } catch (SQLException e) {
            return createErrorResponse(request, "获取住宿信息失败: " + e.getMessage());
        }
    }
    
    private Message handleSubmitApplication(Message request, User user, Map<String, Object> dataMap) {
        if (user == null) {
            return createErrorResponse(request, "用户未登录，请先登录");
        }
        
        if (dataMap == null || !dataMap.containsKey("application")) {
            return createErrorResponse(request, "缺少申请数据");
        }
        
        try {
            Object applicationObj = dataMap.get("application");
            Dorm application;
            
            if (applicationObj instanceof Dorm) {
                application = (Dorm) applicationObj;
            } else {
                String applicationJson = Jsonable.toJson(applicationObj);
                application = Jsonable.fromJson(applicationJson, Dorm.class);
            }
            
            // 设置学生ID
            application.setStudentId(user.getCid());
            
            boolean success = dormService.submitApplication(application);
            Message response = new Message(request.getType());
            
            if (success) {
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData("申请提交成功");
            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("申请提交失败");
            }
            
            return response;
        } catch (Exception e) {
            return createErrorResponse(request, "提交申请失败: " + e.getMessage());
        }
    }
    
    private Message handleGetApplications(Message request, User user) {
        if (user == null) {
            return createErrorResponse(request, "用户未登录，请先登录");
        }
        
        try {
            java.util.List<Dorm> applications = dormService.getApplications(user.getCid());
            Message response = new Message(request.getType());
            response.setStatus(Message.STATUS_SUCCESS);
            response.setData(applications);
            return response;
        } catch (SQLException e) {
            return createErrorResponse(request, "获取申请记录失败: " + e.getMessage());
        }
    }
    
    private Message handleSubmitService(Message request, User user, Map<String, Object> dataMap) {
        if (user == null) {
            return createErrorResponse(request, "用户未登录，请先登录");
        }
        
        if (dataMap == null || !dataMap.containsKey("service")) {
            return createErrorResponse(request, "缺少服务数据");
        }
        
        try {
            Object serviceObj = dataMap.get("service");
            Dorm service;
            
            if (serviceObj instanceof Dorm) {
                service = (Dorm) serviceObj;
            } else {
                String serviceJson = Jsonable.toJson(serviceObj);
                service = Jsonable.fromJson(serviceJson, Dorm.class);
            }
            
            // 设置学生ID
            service.setStudentId(user.getCid());
            
            boolean success = dormService.submitService(service);
            Message response = new Message(request.getType());
            
            if (success) {
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData("服务申请提交成功");
            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("服务申请提交失败");
            }
            
            return response;
        } catch (Exception e) {
            return createErrorResponse(request, "提交服务申请失败: " + e.getMessage());
        }
    }
    
    private Message handleGetServices(Message request, User user) {
        if (user == null) {
            return createErrorResponse(request, "用户未登录，请先登录");
        }
        
        try {
            java.util.List<Dorm> services = dormService.getServices(user.getCid());
            Message response = new Message(request.getType());
            response.setStatus(Message.STATUS_SUCCESS);
            response.setData(services);
            return response;
        } catch (SQLException e) {
            return createErrorResponse(request, "获取服务记录失败: " + e.getMessage());
        }
    }
    
    // 管理端功能处理方法
    
    private Message handleGetAllDormInfo(Message request, User user) {
        if (user == null) {
            return createErrorResponse(request, "用户未登录，请先登录");
        }
        
        if (!"AD".equals(user.getRole())) {
            return createErrorResponse(request, "无权限执行此操作");
        }
        
        try {
            java.util.List<Dorm> dormInfoList = dormService.getAllDormInfo();
            Message response = new Message(request.getType());
            response.setStatus(Message.STATUS_SUCCESS);
            response.setData(dormInfoList);
            return response;
        } catch (SQLException e) {
            return createErrorResponse(request, "获取所有住宿信息失败: " + e.getMessage());
        }
    }
    
    private Message handleGetPendingApplications(Message request, User user) {
        if (user == null) {
            return createErrorResponse(request, "用户未登录，请先登录");
        }
        
        if (!"AD".equals(user.getRole())) {
            return createErrorResponse(request, "无权限执行此操作");
        }
        
        try {
            java.util.List<Dorm> pendingApplications = dormService.getPendingApplications();
            Message response = new Message(request.getType());
            response.setStatus(Message.STATUS_SUCCESS);
            response.setData(pendingApplications);
            return response;
        } catch (SQLException e) {
            return createErrorResponse(request, "获取待审核申请失败: " + e.getMessage());
        }
    }
    
    private Message handleUpdateApplicationStatus(Message request, User user, Map<String, Object> dataMap) {
        if (user == null) {
            return createErrorResponse(request, "用户未登录，请先登录");
        }
        
        if (!"AD".equals(user.getRole())) {
            return createErrorResponse(request, "无权限执行此操作");
        }
        
        if (dataMap == null || !dataMap.containsKey("applicationId") || 
            !dataMap.containsKey("status")) {
            return createErrorResponse(request, "缺少必要参数");
        }
        
        try {
            String applicationId = (String) dataMap.get("applicationId");
            String status = (String) dataMap.get("status");
            String reviewer = user.getName(); // 使用管理员姓名作为审核人
            
            boolean success = dormService.updateApplicationStatus(applicationId, status, reviewer);
            Message response = new Message(request.getType());
            
            if (success) {
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData("申请状态更新成功");
            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("申请状态更新失败");
            }
            
            return response;
        } catch (Exception e) {
            return createErrorResponse(request, "更新申请状态失败: " + e.getMessage());
        }
    }
    
    private Message handleGetAllServices(Message request, User user) {
        if (user == null) {
            return createErrorResponse(request, "用户未登录，请先登录");
        }
        
        if (!"AD".equals(user.getRole())) {
            return createErrorResponse(request, "无权限执行此操作");
        }
        
        try {
            java.util.List<Dorm> allServices = dormService.getAllServices();
            Message response = new Message(request.getType());
            response.setStatus(Message.STATUS_SUCCESS);
            response.setData(allServices);
            return response;
        } catch (SQLException e) {
            return createErrorResponse(request, "获取所有服务记录失败: " + e.getMessage());
        }
    }
    
    private Message handleUpdateServiceStatus(Message request, User user, Map<String, Object> dataMap) {
        if (user == null) {
            return createErrorResponse(request, "用户未登录，请先登录");
        }
        
        if (!"AD".equals(user.getRole())) {
            return createErrorResponse(request, "无权限执行此操作");
        }
        
        if (dataMap == null || !dataMap.containsKey("serviceId") || 
            !dataMap.containsKey("status")) {
            return createErrorResponse(request, "缺少必要参数");
        }
        
        try {
            String serviceId = (String) dataMap.get("serviceId");
            String status = (String) dataMap.get("status");
            String processor = user.getName(); // 使用管理员姓名作为处理人
            
            boolean success = dormService.updateServiceStatus(serviceId, status, processor);
            Message response = new Message(request.getType());
            
            if (success) {
                response.setStatus(Message.STATUS_SUCCESS);
                response.setData("服务状态更新成功");
            } else {
                response.setStatus(Message.STATUS_ERROR);
                response.setData("服务状态更新失败");
            }
            
            return response;
        } catch (Exception e) {
            return createErrorResponse(request, "更新服务状态失败: " + e.getMessage());
        }
    }
    
    // 创建错误响应
    private Message createErrorResponse(Message request, String message) {
        Message response = new Message(request.getType());
        response.setStatus(Message.STATUS_ERROR);
        response.setMessage(message);
        return response;
    }
}
