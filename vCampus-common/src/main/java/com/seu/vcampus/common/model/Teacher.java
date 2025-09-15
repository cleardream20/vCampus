package com.seu.vcampus.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int age; // 年龄
    private String gender; // 性别
    private String address; // 家庭地址
    private String nid; // 身份证号
    private String endate; // 入职时间
    private String title; // 职称
    private String department; // 学院

//    public Teacher() {}
//    // 构造函数 super()
//    public Teacher(String cid, String password, String tsid, String email, String role,
//                   String department, String title) {
//        super(cid, password, tsid, email, role, department, title);
//        this.department = department;
//        this.title = title;
//    }
}
