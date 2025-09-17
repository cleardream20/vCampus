package com.seu.vcampus.common.model;

import com.seu.vcampus.common.util.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends User implements Serializable, Jsonable {
    private static final long serialVersionUID = 1L;
    private int age; // 年龄
    private String gender; // 性别
    private String address; // 家庭地址
    private String nid; // 身份证号
    private String endate; // 入职时间
    private String title; // 职称
    private String department; // 学院

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
    }
}
