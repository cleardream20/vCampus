package com.seu.vcampus.server.controller;

import com.seu.vcampus.server.service.DormService;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.DormMessage;
import com.seu.vcampus.common.model.Dorm;

import java.util.List;
import java.util.Map;

public class DormController {
    private final DormService dormService;

    public DormController(DormService dormService) {
        this.dormService = dormService;
    }

    public Message handleRequest( Message request) {
        Message response = new Message();
        response.setType(request.getType());

        try {
            switch (request.getType()) {
                // 学生端请求
                case DormMessage.GET_DORM_INFO:
                    String studentId = (String) request.getData();
                    Dorm dorm = dormService.getDormInfo(studentId);
                    response.setStatus(dorm != null ? Message.STATUS_SUCCESS : Message.STATUS_ERROR);
                    response.setData(dorm);
                    break;
                    
                case DormMessage.GET_APPLICATIONS:
                    String studentIdForApplications = (String) request.getData();
                    List<Dorm> applications = dormService.getStudentApplications(studentIdForApplications);
                    response.setStatus(Message.STATUS_SUCCESS);
                    response.setData(applications);
                    break;
                    
                case DormMessage.APPLY_CHECK_IN:
                case DormMessage.APPLY_CHECK_OUT:
                case DormMessage.APPLY_ADJUST:
                    Dorm application = (Dorm) request.getData();
                    boolean appResult = dormService.submitApplication(application);
                    response.setStatus(appResult ? Message.STATUS_SUCCESS : Message.STATUS_ERROR);
                    response.setData(appResult ? "申请提交成功" : "申请提交失败");
                    break;
                    
                case DormMessage.SUBMIT_REPAIR:
                case DormMessage.SUBMIT_COMPLAINT:
                    Dorm serviceRequest = (Dorm) request.getData();
                    boolean serviceResult = dormService.submitServiceRequest(serviceRequest);
                    response.setStatus(serviceResult ? Message.STATUS_SUCCESS : Message.STATUS_ERROR);
                    response.setData(serviceResult ? "服务请求提交成功" : "服务请求提交失败");
                    break;
                    
                case DormMessage.GET_SERVICE_STATUS:
                    String studentIdForServices = (String) request.getData();
                    List<Dorm> services = dormService.getStudentServiceRequests(studentIdForServices);
                    response.setStatus(Message.STATUS_SUCCESS);
                    response.setData(services);
                    break;
                    
                // 管理端请求
                case DormMessage.GET_ALL_DORM_INFO:
                    List<Dorm> allDorms = dormService.getAllDorms();
                    response.setStatus(Message.STATUS_SUCCESS);
                    response.setData(allDorms);
                    break;
                    
                case DormMessage.UPDATE_DORM_INFO:
                    Dorm updatedDorm = (Dorm) request.getData();
                    boolean updateResult = dormService.updateDorm(updatedDorm);
                    response.setStatus(updateResult ? Message.STATUS_SUCCESS : Message.STATUS_ERROR);
                    response.setData(updateResult ? "住宿信息更新成功" : "住宿信息更新失败");
                    break;
                    
                case DormMessage.GET_PENDING_APPLICATIONS:
                    List<Dorm> pendingApplications = dormService.getPendingApplications();
                    response.setStatus(Message.STATUS_SUCCESS);
                    response.setData(pendingApplications);
                    break;
                    
                case DormMessage.APPROVE_APPLICATION:
                    Map<String, String> approveData = (Map<String, String>) request.getData();
                    boolean approveResult = dormService.approveApplication(
                        approveData.get("studentId"),
                        approveData.get("reviewer"),
                        approveData.get("remarks")
                    );
                    response.setStatus(approveResult ? Message.STATUS_SUCCESS : Message.STATUS_ERROR);
                    response.setData(approveResult ? "申请已批准" : "批准操作失败");
                    break;
                    
                case DormMessage.REJECT_APPLICATION:
                    Map<String, String> rejectData = (Map<String, String>) request.getData();
                    boolean rejectResult = dormService.rejectApplication(
                        rejectData.get("studentId"),
                        rejectData.get("reviewer"),
                        rejectData.get("remarks")
                    );
                    response.setStatus(rejectResult ? Message.STATUS_SUCCESS : Message.STATUS_ERROR);
                    response.setData(rejectResult ? "申请已拒绝" : "拒绝操作失败");
                    break;
                    
                case DormMessage.GET_ALL_SERVICES:
                    List<Dorm> allServices = dormService.getAllServiceRequests();
                    response.setStatus(Message.STATUS_SUCCESS);
                    response.setData(allServices);
                    break;
                    
                case DormMessage.PROCESS_SERVICE:
                    Map<String, String> processData = (Map<String, String>) request.getData();
                    boolean processResult = dormService.processServiceRequest(
                        processData.get("studentId"),
                        processData.get("processor")
                    );
                    response.setStatus(processResult ? Message.STATUS_SUCCESS : Message.STATUS_ERROR);
                    response.setData(processResult ? "服务已开始处理" : "处理操作失败");
                    break;
                    
                default:
                    response.setStatus(Message.STATUS_ERROR);
                    response.setData("未知的请求类型: " + request.getType());
            }
        } catch (Exception e) {
            response.setStatus(Message.STATUS_ERROR);
            response.setData("处理请求时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }
}
