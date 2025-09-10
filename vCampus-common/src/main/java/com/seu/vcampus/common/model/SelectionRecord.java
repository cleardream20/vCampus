package com.seu.vcampus.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectionRecord implements Serializable {
    private String studentId;
    private String courseId;
    private String term;
    private Date selectTime;
}