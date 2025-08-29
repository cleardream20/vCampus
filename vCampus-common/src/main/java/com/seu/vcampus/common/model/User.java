package com.seu.vcampus.common.model;

import java.io.Serializable;

public class User implements Serializable {
    private String cid; // card id 一卡通号
    private String password; // 密码
    private String tsid; // sid学生学号 + tid教职工号
    private Integer email; // 邮箱辅助找回密码
    private String role; // 角色：ST | TC | AD

    public User() {}

    public User(String cid, String password, String tsid, Integer email, String role) {
        this.cid = cid;
        this.password = password;
        this.tsid = tsid;
        this.email = email;
        this.role = role;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTsid() {
        return tsid;
    }

    public void setTsid(String tsid) {
        this.tsid = tsid;
    }

    public Integer getEmail() {
        return email;
    }

    public void setEmail(Integer email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Use{" +
                "cid = " + cid + '\'' +
                ", tsid = " + tsid + '\'' +
                ", role = " + role + '\'' +
                "}";
    }
}
