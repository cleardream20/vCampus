package com.seu.vcampus.common.model.course;

import com.seu.vcampus.common.util.Jsonable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectionRecord implements Serializable, Jsonable {
    private String recordId; // 对应RecordID (自动编号)
    private String studentId; // 对应StudentID (短文本)
    private String studentName; // 对应StudentName (短文本)
    private String courseId; // 对应CourseID (短文本)
    private String courseName; // 对应CourseName (短文本)
    private LocalDateTime selectionTime; // 对应SelectionTime (日期/时间)
    private String department; // 对应Department (短文本)
}