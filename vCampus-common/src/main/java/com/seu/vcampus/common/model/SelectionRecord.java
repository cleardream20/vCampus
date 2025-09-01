package com.seu.vcampus.common.model;

import java.io.Serializable;
import java.util.Date;

public class SelectionRecord implements Serializable {
    private String studentId;
    private String courseId;
    private String term;
    private Date selectTime;
}