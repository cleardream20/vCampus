package com.seu.vcampus.client.controller.student;


import java.util.Map;
import java.util.Random;

import com.seu.vcampus.common.model.Student;

//    private String cid; // card id 一卡通号
//    private String tsid; // sid学生学号 + tid教职工号
//    private String name; // 姓名
//    private String phone; // 电话号码
//    private String sex;
//    private String birthday; // 出生日期 YY-MM-DD
//    private String address;
//    private String nid;
//    private String endate;
//    private String grade;
//    private String major;
//    private String stid; // 学籍号
//    private String es; // 学制 Education System 三年、四年、5+3本博 等
//    private String esState; // 学籍状态 在籍、休学、退学或毕业


public class ADController {

    public Object[][] getRandomStudent(int num)
    {
        Random rand = new Random();
        Object[][] students = new Object[num][14];
        for(int i = 0; i < num; i++){
            Student student = new Student().randomStudent();
            students[i] = student.getData();
        }
        return students;
    }

    public String[] getColumnNames() {
        return new String[] {"一卡通号","身份证号","学号","姓名","性别","电话号码","出生日期","家庭住址","入学日期","学籍号","学院","年级","学制","学籍状态"};
    }

    public Object[][] getDataWithFilters(Map<Integer, String> filters) {
        Random rand = new Random();
        int num = rand.nextInt(101);
        return getRandomStudent(num);
    }
}
