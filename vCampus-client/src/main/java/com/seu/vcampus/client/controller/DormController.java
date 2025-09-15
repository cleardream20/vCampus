package com.seu.vcampus.client.controller;

import com.seu.vcampus.client.socket.ClientSocketUtil;
import com.seu.vcampus.common.model.Dorm;
import com.seu.vcampus.common.util.DormMessage;
import com.seu.vcampus.common.util.Message;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DormController {
    private String currentUserId;

    public DormController() {}

    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }

    // 获取当前学生的住宿信息
    public Dorm getDormInfo() {
        try {
            Message request = createRequest(DormMessage.GET_DORM_INFO);
            Message response = ClientSocketUtil.sendRequest(request);

            return processDormResponse(response);
        } catch (Exception e) {
            System.err.println("获取住宿信息失败: " + e.getMessage());
            return null;
        }
    }

    // 获取当前学生的所有住宿申请
    public List<Dorm> getApplications() {
        try {
            Message request = createRequest(DormMessage.GET_APPLICATIONS);
            Message response = ClientSocketUtil.sendRequest(request);

            return processDormListResponse(response);
        } catch (Exception e) {
            System.err.println("获取申请记录失败: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 提交住宿申请（入住、调换、退宿）
    public boolean submitApplication(Dorm application) {
        try {
            Message request = createRequest(DormMessage.SUBMIT_APPLICATION);
            // 将申请数据设置到消息中
            request.setData(application);
            Message response = ClientSocketUtil.sendRequest(request);

            return processOperationResponse(response);
        } catch (Exception e) {
            System.err.println("提交申请失败: " + e.getMessage());
            return false;
        }
    }

    // 获取当前学生的所有服务申请
    public List<Dorm> getServices() {
        try {
            Message request = createRequest(DormMessage.GET_SERVICES);
            Message response = ClientSocketUtil.sendRequest(request);

            return processDormListResponse(response);
        } catch (Exception e) {
            System.err.println("获取服务记录失败: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 提交宿舍服务申请
    public boolean submitService(Dorm service) {
        try {
            Message request = createRequest(DormMessage.SUBMIT_SERVICE);
            // 将服务数据设置到消息中
            request.setData(service);
            Message response = ClientSocketUtil.sendRequest(request);

            return processOperationResponse(response);
        } catch (Exception e) {
            System.err.println("提交服务申请失败: " + e.getMessage());
            return false;
        }
    }

    // 管理端：获取所有学生的住宿信息
    public List<Dorm> getAllDormInfo() {
        try {
            Message request = createRequest(DormMessage.GET_ALL_DORM_INFO);
            Message response = ClientSocketUtil.sendRequest(request);

            return processDormListResponse(response);
        } catch (Exception e) {
            System.err.println("获取所有住宿信息失败: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 管理端：获取待审核的住宿申请
    public List<Dorm> getPendingApplications() {
        try {
            Message request = createRequest(DormMessage.GET_PENDING_APPLICATIONS);
            Message response = ClientSocketUtil.sendRequest(request);

            return processDormListResponse(response);
        } catch (Exception e) {
            System.err.println("获取待审核申请失败: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 管理端：更新申请状态（批准/拒绝）
    public boolean updateApplicationStatus(Dorm application) {
        try {
            Message request = createRequest(DormMessage.UPDATE_APPLICATION_STATUS);
            // 将申请数据设置到消息中
            request.setData(application);
            Message response = ClientSocketUtil.sendRequest(request);

            return processOperationResponse(response);
        } catch (Exception e) {
            System.err.println("更新申请状态失败: " + e.getMessage());
            return false;
        }
    }

    // 管理端：获取所有服务申请
    public List<Dorm> getAllServices() {
        try {
            Message request = createRequest(DormMessage.GET_ALL_SERVICES);
            Message response = ClientSocketUtil.sendRequest(request);

            return processDormListResponse(response);
        } catch (Exception e) {
            System.err.println("获取所有服务申请失败: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // 管理端：更新服务状态
    public boolean updateServiceStatus(Dorm service) {
        try {
            Message request = createRequest(DormMessage.UPDATE_SERVICE_STATUS);
            // 将服务数据设置到消息中
            request.setData(service);
            Message response = ClientSocketUtil.sendRequest(request);

            return processOperationResponse(response);
        } catch (Exception e) {
            System.err.println("更新服务状态失败: " + e.getMessage());
            return false;
        }
    }

    // 处理单个Dorm对象的响应
    private Dorm processDormResponse(Message response) {
        if (response == null) {
            System.err.println("获取响应超时");
            return null;
        }

        if (Objects.equals(response.getStatus(), Message.STATUS_SUCCESS)) {
            return (Dorm) response.getData();
        } else {
            System.err.println("服务器错误: " + response.getMessage());
            return null;
        }
    }

    // 处理Dorm对象列表的响应
    private List<Dorm> processDormListResponse(Message response) {
        if (response == null) {
            System.err.println("获取响应超时");
            return Collections.emptyList();
        }

        if (Objects.equals(response.getStatus(), Message.STATUS_SUCCESS)) {
            return (List<Dorm>) response.getData();
        } else {
            System.err.println("服务器错误: " + response.getMessage());
            return Collections.emptyList();
        }
    }

    // 处理操作响应
    private boolean processOperationResponse(Message response) {
        if (response == null) {
            System.err.println("操作响应超时");
            return false;
        }

        return Objects.equals(response.getStatus(), Message.STATUS_SUCCESS);
    }

    // 创建请求消息
    private Message createRequest(String type) {
        Message request = new Message();
        request.setType(type);
        request.setSender(currentUserId);
        return request;
    }
}
