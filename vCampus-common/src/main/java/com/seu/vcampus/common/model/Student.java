package com.seu.vcampus.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//public class User implements Serializable {
//    private String cid; // card id 一卡通号
//    private String password; // 密码
//    private String tsid; // sid学生学号 + tid教职工号
//    private String name; // 姓名
//    private String email; // 邮箱辅助找回密码
//    private String phone; // 电话号码
//    private String role; // 角色：ST | TC | AD
//}


//在校大学生学籍：包括姓名、性别、出生日期、学籍号、专业、入学时间、学制等。
//
//学籍状态：如是否在籍、休学、退学或毕业等。

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student extends User implements Serializable {
    private String sex;
    private String birthday; // 出生日期 YY-MM-DD
    private String address;
    private String nid;
    private String endate;
    private String grade;
    private String major;
    private String stid; // 学籍号
    private String es; // 学制 Education System 三年、四年、5+3本博 等
    private String esState; // 学籍状态 在籍、休学、退学或毕业
}
