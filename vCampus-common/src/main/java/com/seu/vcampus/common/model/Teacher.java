package com.seu.vcampus.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String age;
    private String sex;
    private String address;
    private String nid;
    private String endate;
    private String title;
    private String department;

//    public Teacher() {}
//    // 构造函数 super()
//    public Teacher(String cid, String password, String tsid, String email, String role,
//                   String department, String title) {
//        super(cid, password, tsid, email, role, department, title);
//        this.department = department;
//        this.title = title;
//    }
}
