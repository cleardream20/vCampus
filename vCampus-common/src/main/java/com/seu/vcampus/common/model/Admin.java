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
@AllArgsConstructor
public class Admin extends User implements Serializable, Jsonable {
    private String modules;
    private List<String> moduleList;

    public Admin(User user, String modules) {
        super(user.getCid(), user.getPassword(), user.getTsid(), user.getName(),
                user.getEmail(), user.getPhone(), user.getRole());
        this.modules = modules;
        this.moduleList = modulesToList(modules);
    }

    public Admin(User user, List<String> moduleList) {
        super(user.getCid(), user.getPassword(), user.getTsid(), user.getName(),
                user.getEmail(), user.getPhone(), user.getRole());
        this.moduleList = moduleList;
        this.modules = listToModules(moduleList);
    }

    // modules: "User|Library|Shop" 转换为 List: ["User", "Library", "Shop"]
    public List<String> modulesToList(String modules) {
        if (modules == null || modules.trim().isEmpty()) {
            return List.of();
        }
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

    /**
     * 设置模块字符串并自动更新列表
     */
    public void setModules(String modules) {
        this.modules = modules;
        this.moduleList = modulesToList(modules);
    }

    /**
     * 设置模块列表并自动更新字符串
     */
    public void setModuleList(List<String> moduleList) {
        this.moduleList = moduleList;
        this.modules = listToModules(moduleList);
    }

    /**
     * 添加单个模块
     */
    public void addModule(String module) {
        if (moduleList == null) {
            moduleList = new ArrayList<>();
        }
        if (module != null && !module.trim().isEmpty() && !moduleList.contains(module)) {
            moduleList.add(module);
            this.modules = listToModules(moduleList);
        }
    }

    /**
     * 移除单个模块
     */
    public void removeModule(String module) {
        if (moduleList != null && module != null) {
            moduleList.remove(module);
            this.modules = listToModules(moduleList);
        }
    }

    /**
     * 检查是否包含某个模块
     */
    public boolean hasModule(String module) {
        if (modules.equals("All")) return true;
        return moduleList != null && moduleList.contains(module);
    }
}