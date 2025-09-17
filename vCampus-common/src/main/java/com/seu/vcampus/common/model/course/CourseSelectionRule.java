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
public class CourseSelectionRule implements Serializable, Jsonable {
    private String batchName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxCredits;
    private String prerequisites;
    private Boolean conflictCheck;

    public CourseSelectionRule(CourseSelectionRule currentRule) {
        this.batchName = currentRule.batchName;
        this.startTime = currentRule.startTime;
        this.endTime = currentRule.endTime;
        this.maxCredits = currentRule.maxCredits;
        this.prerequisites = currentRule.prerequisites;
        this.conflictCheck = currentRule.conflictCheck;
    }
}