package com.seu.vcampus.common.model;

import com.seu.vcampus.common.util.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
//@AllArgsConstructor
public class Teacher extends User implements Serializable, Jsonable {
    private static final long serialVersionUID = 1L;
    private int age; // 年龄
    private String gender; // 性别
    private String address; // 家庭地址
    private String nid; // 身份证号
    private String endate; // 入职时间
    private String title; // 职称
    private String department; // 学院
    private String curRole;
    private String modules;
    private List<String> moduleList;

    public Teacher(String cid, String password, String tsid, String name, String email, String phone, String role,
                   int age, String gender, String address, String nid, String endate, String title, String department) {
        super(cid, password, tsid, name, email, phone, role);
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.nid = nid;
        this.endate = endate;
        this.title = title;
        this.department = department;
        this.curRole = "TC";
        this.setModules("");
    }

    public Teacher(String cid, String password, String tsid, String name, String email, String phone, String role,
                   int age, String gender, String address, String nid, String endate, String title, String department, String curRole, String modules) {
        super(cid, password, tsid, name, email, phone, role);
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.nid = nid;
        this.endate = endate;
        this.title = title;
        this.department = department;
        this.curRole = curRole;
        this.setModules(modules);
    }

    // 默认不为管理员
    public Teacher(User user, int age, String gender, String address, String nid, String endate, String title, String department) {
        super(user.getCid(), user.getPassword(), user.getTsid(), user.getName(),
                user.getEmail(), user.getPhone(), user.getRole());
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.nid = nid;
        this.endate = endate;
        this.title = title;
        this.department = department;
        this.curRole = "TC";
        this.setModules("");
    }

    public Teacher(User user, int age, String gender, String address, String nid, String endate, String title, String department, String curRole, String modules) {
        super(user.getCid(), user.getPassword(), user.getTsid(), user.getName(),
                user.getEmail(), user.getPhone(), user.getRole());
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.nid = nid;
        this.endate = endate;
        this.title = title;
        this.department = department;
        this.curRole = curRole;
        this.setModules(modules);
    }

    // modules: "User|Library|Shop" 转换为 List: ["User", "Library", "Shop"]
    public List<String> modulesToList(String modules) {
        if (modules == null || modules.trim().isEmpty()) {
            return List.of();
        }
        System.out.println("初始化时modules: " +  modules);
        return Arrays.stream(modules.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    // moduleList: ["User", "Library"] 转换为 String: "User|Library"
    public String listToModules(List<String> moduleList) {
        if (moduleList == null || moduleList.isEmpty()) {
            return "";
        }
        return moduleList.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .collect(Collectors.joining("|"));
    }

    // 为了方便使用，可以添加一些辅助方法
    public void setModules(String modules) {
        this.modules = modules;
        this.moduleList = modulesToList(modules);
    }

    public void setModuleList(List<String> moduleList) {
        this.moduleList = moduleList;
        this.modules = listToModules(moduleList);
    }

    public void addModule(String module) {
        if (moduleList == null) {
            moduleList = new ArrayList<>();
        }
        if (module != null && !module.trim().isEmpty() && !moduleList.contains(module)) {
            moduleList.add(module);
            this.modules = listToModules(moduleList);
        }
    }

    public void removeModule(String module) {
        if (moduleList != null && module != null) {
            moduleList.remove(module);
            this.modules = listToModules(moduleList);
        }
    }

    public boolean hasModule(String module) {
        if (modules.equals("All")) return true;
        return moduleList != null && moduleList.contains(module);
    }
}
