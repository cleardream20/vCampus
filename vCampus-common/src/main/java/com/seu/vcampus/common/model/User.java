package com.seu.vcampus.common.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private String email;
    private String password;
    private Integer age;
    private String role; // 角色：ST | TC | AD

    public User() {}

    public User(String id, String name, String email, String password, Integer age, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id : " + id + '\'' +
                ", name : " + name + '\'' +
                ", email : " + email + '\'' +
                ", password : " + password + '\'' +
                ", age : " + age + '\'' +
                ", role : " + role + '\'' +
                "}";
    }
}
