package com.seu.vcampus.common.model;

import com.seu.vcampus.common.util.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.io.Serializable;
import java.util.Map;

//public class User implements Serializable {
//    private String cid; // card id 一卡通号
//    private String password; // 密码
//    private String tsid; // sid学生学号 + tid教职工号
//    private String name; // 姓名
//    private String email; // 邮箱辅助找回密码
//    private String phone; // 电话号码
//    private String role; // 角色：ST | TC | AD
//}

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student extends User implements Serializable, Jsonable {

    public Student(String cid, String password, String tsid, String name,
                   String email, String phone, String role,
                   String sex, String birthday, String address, String nid,
                   String endate, String grade, String major, String stid,
                   String es, String esState, int age) {
        super(cid, password, tsid, name, email, phone, role);
        this.sex = sex;
        this.birthday = birthday;
        this.address = address;
        this.nid = nid;
        this.endate = endate;
        this.grade = grade;
        this.major = major;
        this.stid = stid;
        this.es = es;
        this.esState = esState;
        this.age = age;
    }

    public Student(User user, String sex, String birthday, String address,
                   String nid, String endate, String grade, String major,
                   String stid, String es, String esState, int age) {
        super(user.getCid(), user.getPassword(), user.getTsid(), user.getName(),
                user.getEmail(), user.getPhone(), user.getRole());
        this.sex = sex;
        this.birthday = birthday;
        this.address = address;
        this.nid = nid;
        this.endate = endate;
        this.grade = grade;
        this.major = major;
        this.stid = stid;
        this.es = es;
        this.esState = esState;
        this.age = age;
    }

    public Student(Map<Integer, JTextField> map) {
        //"姓名","电话","邮箱","性别","年龄","出生日期","家庭住址","身份证号","入学日期","年级","专业","学籍号","学制","学籍状态"
        super(null, null, null, map.get(0).getText(), map.get(2).getText(), map.get(1).getText(), "ST");
        this.sex = map.get(3).getText();
        this.birthday = map.get(5).getText();
        this.address = map.get(6).getText();
        this.nid = map.get(7).getText();
        this.endate = map.get(8).getText();
        this.grade = map.get(9).getText();
        this.major = map.get(10).getText();
        this.stid = map.get(11).getText();
        this.es = map.get(12).getText();
        this.esState = map.get(13).getText();
        this.age = Integer.parseInt(map.get(4).getText());
    }

    public Object[] getRow() {
//      "一卡通号","身份证号","学号","姓名","性别","电话号码","出生日期","家庭住址","入学日期","学籍号","学院","年级","学制","学籍状态"
        return new String[] {getCid(), nid, getTsid(), getName(), sex, getPhone(), birthday, address, endate, stid, major, grade, es, esState, String.valueOf(age)};
    }

    private String sex;
    private int age;
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