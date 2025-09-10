package com.seu.vcampus.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student extends User implements Serializable {
    private String sex;
    private String age;
    private String address;
    private String nid;
    private String endate;
    private String grade;
    private String major;
    private Long dormId; 
}
