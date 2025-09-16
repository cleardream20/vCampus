package com.seu.vcampus.client.service;

import com.seu.vcampus.client.socket.ClientSocketUtil;
import com.seu.vcampus.common.model.*;
import com.seu.vcampus.common.util.Jsonable;
import com.seu.vcampus.common.util.Message;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private Map<String, VerificationCode> codeStore = new ConcurrentHashMap<>();

    private boolean getCheckResponse(Message request, String typeMessage) {
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (response == null) {
                System.err.println(typeMessage + "失败：服务器无响应");
                return false;
            }
            if (response.isSuccess()) {
                System.out.println(typeMessage + "成功：" + response.getMessage());
                return true;
            } else {
                System.out.println(typeMessage + "失败" + response.getMessage());
            }
        } catch (IOException e) {
            System.err.println(typeMessage + "失败：无法连接服务器 - " + e.getMessage());
            return false;
        }
        return false;
    }

    // 非空检查在前端界面进行
    public boolean addUser(User user) {
        if (user == null) {
            System.err.println("新增用户失败：用户不能为空");
            return false;
        }

        Message request = Message.success(Message.ADD_USER, user, "新增用户");
        return getCheckResponse(request, "新增");
    }

    public User getUser(String cid) {
        if (cid == null || cid.isEmpty()) {
            System.err.println("查询用户失败：一卡通号不能为空");
            return null;
        }

        Message request = Message.success(Message.GET_USER, cid, "根据一卡通号查询用户");
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (response == null) {
                System.err.println("更新失败：服务器无响应");
                return null;
            }
            if (response.isSuccess()) {
                System.out.println("更新成功：" + response.getMessage());
                return Jsonable.fromJson(
                        Jsonable.toJson(response.getData()),
                        User.class
                );
            } else {
                System.out.println("更新失败" + response.getMessage());
            }
        } catch (IOException e) {
            System.err.println("更新失败：无法连接服务器 - " + e.getMessage());
            return null;
        }
        return null;
    }

    public boolean updateUser(User user) {
        if (user == null) {
            System.err.println("更新失败：用户信息为空");
        }

        Message request = Message.success(Message.UPDATE_USER, user, "用户更新请求");
        return getCheckResponse(request, "更新");
    }

    public boolean deleteUser(String cid) {
        if (cid == null || cid.isEmpty()) {
            System.err.println("删除用户和失败：一卡通号不能为空");
            return false;
        }

        Message request = Message.success(Message.DELETE_USER, cid, "用户删除请求");
        return getCheckResponse(request, "删除");
    }

    public User getUserByEmail(String email) {
        return new User();
    }

    public User getUserByPhone(String phone) {
        return new User();
    }

    public String generateVerificationCode(String target) {
        String code = "123456";
        VerificationCode vc = new VerificationCode(target, code, 60); // 1分钟
        codeStore.put(target, vc);
        return code;
    }

    public boolean verifyCode(String target, String inputCode) {
        VerificationCode vc = codeStore.get(target);
        if (vc == null) {
            return false;
        }

        if (vc.isValid(inputCode)) {
            codeStore.remove(target); // 验证成功后立即失效（一次性）
            return true;
        } else {
            vc.incrementAttempt();
            if (vc.isExpired() || vc.getAttemptCount() >= 5) {
                codeStore.remove(target); // 失效或尝试过多，清除
            }
            return false;
        }
    }

    public Student getStudentByUser(User user) {
        return new Student();
    }

    public Teacher getTeacherByUser(User user) {
        return new Teacher();
    }

    public Admin getAdminByUser(User user) {
        return new Admin();
    }
}
