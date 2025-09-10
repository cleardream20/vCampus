package com.seu.vcampus.common.model;

import com.seu.vcampus.common.util.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable, Jsonable {
    private String cid; // card id 一卡通号
    private String password; // 密码
    private String tsid; // sid学生学号 + tid教职工号
    private String name; // 姓名
    private String email; // 邮箱辅助找回密码
    private String phone; // 电话号码
    private String role; // 角色：ST | TC | AD
}
