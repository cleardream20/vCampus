package com.seu.vcampus.client.service;

import com.google.gson.reflect.TypeToken;
import com.seu.vcampus.client.socket.ClientSocketUtil;
import com.seu.vcampus.common.model.*;
import com.seu.vcampus.common.util.Jsonable;
import com.seu.vcampus.common.util.Message;
import com.seu.vcampus.common.util.UserMessage;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
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
        if (email == null || email.isEmpty()) {
            System.err.println("邮箱不能为空");
            return null;
        }
        Message request = Message.success(UserMessage.GET_USER_BY_EMAIL, email, "通过邮箱获取用户");
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (response == null) {
                System.err.println("通过邮箱获取用户失败：服务器无响应");
                return null;
            }
            if (response.isSuccess()) {
                System.out.println("通过邮箱获取用户成功：" + response.getMessage());
                return Jsonable.fromJson(
                        Jsonable.toJson(response.getData()),
                        User.class
                );
            } else {
                System.out.println("通过邮箱获取用户失败" + response.getMessage());
            }
        } catch (IOException e) {
            System.err.println("通过邮箱获取用户失败：无法连接服务器 - " + e.getMessage());
            return null;
        }
        return null;
    }

    public User getUserByPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            System.err.println("电话不能为空");
            return null;
        }
        Message request = Message.success(UserMessage.GET_USER_BY_PHONE, phone, "通过电话获取用户");
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (response == null) {
                System.err.println("通过电话获取用户失败：服务器无响应");
                return null;
            }
            if (response.isSuccess()) {
                System.out.println("通过电话获取用户成功：" + response.getMessage());
                return Jsonable.fromJson(
                        Jsonable.toJson(response.getData()),
                        User.class
                );
            } else {
                System.out.println("通过电话获取用户失败" + response.getMessage());
            }
        } catch (IOException e) {
            System.err.println("通过电话获取用户失败：无法连接服务器 - " + e.getMessage());
            return null;
        }
        return null;
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
        if (user == null) {
            System.err.println("获取学生信息时用户不能为空");
            return null;
        }
        Message request = Message.success(UserMessage.GET_ST_BY_USER, user, "根据用户获取学生信息");
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (response == null) {
                System.err.println("服务器异常，获取学生信息失败");
                return null;
            }
            if (response.isSuccess()) {
                return Jsonable.fromJson(Jsonable.toJson(response.getData()), Student.class);
            } else {
                System.err.println("未获取到学生信息");
                return  null;
            }
        } catch (IOException e) {
            System.err.println("获取学生信息失败，服务器异常：" + e.getMessage());
            return null;
        }
    }

    public Teacher getTeacherByUser(User user) {
        if (user == null) {
            System.err.println("获取教师信息时用户不能为空");
            return null;
        }
        Message request = Message.success(UserMessage.GET_TC_BY_USER, user, "根据用户获取学生信息");
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (response == null) {
                System.err.println("服务器异常，获取教师信息失败");
                return null;
            }
            if (response.isSuccess()) {
                return Jsonable.fromJson(Jsonable.toJson(response.getData()), Teacher.class);
            } else {
                System.err.println("未获取到教师信息");
                return  null;
            }
        } catch (IOException e) {
            System.err.println("获取教师信息失败，服务器异常：" + e.getMessage());
            return null;
        }
    }

    public Admin getAdminByUser(User user) {
        if (user == null) {
            System.err.println("获取管理员信息时用户不能为空");
            return null;
        }
        Message request = Message.success(UserMessage.GET_AD_BY_USER, user, "根据用户获取学生信息");
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (response == null) {
                System.err.println("服务器异常，获取管理员信息失败");
                return null;
            }
            if (response.isSuccess()) {
                return Jsonable.fromJson(Jsonable.toJson(response.getData()), Admin.class);
            } else {
                System.err.println("未获取到管理员信息");
                return  null;
            }
        } catch (IOException e) {
            System.err.println("获取管理员信息失败，服务器异常：" + e.getMessage());
            return null;
        }
    }




    public List<User> getAllUsers() {
        Message request = Message.success(UserMessage.GET_ALL_USER, null, "获取用户列表");
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (response == null) {
                System.err.println("服务器异常，获取所有用户信息失败");
                return null;
            }
            if (response.isSuccess()) {
                return Jsonable.fromJson(Jsonable.toJson(response.getData()), new TypeToken<List<User>>() {}.getType());
            } else {
                System.err.println("未获取到所有用户信息");
                return null;
            }
        } catch (IOException e) {
            System.err.println("获取所有用户信息失败，服务器异常：" + e.getMessage());
            return null;
        }
    }

    public List<UserRequest> getPendingRequests() { /* 获取待审批请求 */ return Collections.emptyList();}
    public void approveRequest(UserRequest req) { }
    public void rejectRequest(UserRequest req) { }

    public Teacher getTeacher(String cid) {
        if(cid == null || cid.isEmpty()) {
            System.err.println("获取教师失败：一卡通号不能为空");
            return null;
        }
        Message request = Message.success(UserMessage.GET_TC, cid, "获取教师请求");
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (response == null) {
                System.err.println("更新教师失败：服务器无响应");
                return null;
            }
            if (response.isSuccess()) {
                System.out.println("更新教师成功：" + response.getMessage());
                return Jsonable.fromJson(
                        Jsonable.toJson(response.getData()),
                        Teacher.class
                );
            } else {
                System.out.println("更新教师失败" + response.getMessage());
            }
        } catch (IOException e) {
            System.err.println("更新教师失败：无法连接服务器 - " + e.getMessage());
            return null;
        }
        return null;
    }

    public boolean addTeacher(Teacher teacher) {
        if (teacher == null) {
            System.err.println("更新失败：教师信息为空");
        }

        Message request = Message.success(UserMessage.ADD_TC, teacher, "教师更新请求");
        return getCheckResponse(request, "教师更新");
    }

    public boolean updateTeacher(Teacher teacher) {
        if (teacher == null) {
            System.err.println("更新失败：教师信息为空");
        }

        Message request = Message.success(UserMessage.UPDATE_TC, teacher, "教师更新请求");
        return getCheckResponse(request, "教师更新");
    }

    public boolean deleteTeacher(String cid) {
        if (cid == null || cid.isEmpty()) {
            System.err.println("删除教师失败：一卡通号不能为空");
            return false;
        }

        Message request = Message.success(UserMessage.DELETE_TC, cid, "教师删除请求");
        return getCheckResponse(request, "删除教师");
    }

    public Teacher getAdmin(String cid) {
        if(cid == null || cid.isEmpty()) {
            System.err.println("获取管理员失败：一卡通号不能为空");
            return null;
        }
        Message request = Message.success(UserMessage.GET_AD, cid, "获取管理员请求");
        try {
            Message response = ClientSocketUtil.sendRequest(request);
            if (response == null) {
                System.err.println("更新管理员失败：服务器无响应");
                return null;
            }
            if (response.isSuccess()) {
                System.out.println("更新管理员成功：" + response.getMessage());
                return Jsonable.fromJson(
                        Jsonable.toJson(response.getData()),
                        Teacher.class
                );
            } else {
                System.out.println("更新管理员失败" + response.getMessage());
            }
        } catch (IOException e) {
            System.err.println("更新管理员失败：无法连接服务器 - " + e.getMessage());
            return null;
        }
        return null;
    }

    public boolean addAdmin(Admin admin) {
        if (admin == null) {
            System.err.println("更新失败：管理员信息为空");
        }

        Message request = Message.success(UserMessage.ADD_AD, admin, "管理员更新请求");
        return getCheckResponse(request, "管理员更新");
    }

    public boolean updateAdmin(Admin admin) {
        if (admin == null) {
            System.err.println("更新失败：管理员信息为空");
        }

        Message request = Message.success(UserMessage.UPDATE_AD, admin, "管理员更新请求");
        return getCheckResponse(request, "管理员更新");
    }

    public boolean deleteAdmin(String cid) {
        if (cid == null || cid.isEmpty()) {
            System.err.println("删除管理员失败：一卡通号不能为空");
            return false;
        }

        Message request = Message.success(UserMessage.DELETE_AD, cid, "管理员删除请求");
        return getCheckResponse(request, "删除管理员");
    }
}
