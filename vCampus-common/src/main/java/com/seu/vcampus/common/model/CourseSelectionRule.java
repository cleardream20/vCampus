package com.seu.vcampus.common.model;

import java.time.LocalDateTime;

public class CourseSelectionRule {
    private String batchName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxCredits;
    private String prerequisites;
    private Boolean conflictCheck;

    public CourseSelectionRule() {
        this.batchName = "";
        this.startTime = LocalDateTime.now();
        this.endTime = LocalDateTime.now();
        this.maxCredits = 0;
        this.prerequisites = "";
        this.conflictCheck = false;
    }
    public CourseSelectionRule(CourseSelectionRule currentRule) {
        this.batchName = currentRule.batchName;
        this.startTime = currentRule.startTime;
        this.endTime = currentRule.endTime;
        this.maxCredits = currentRule.maxCredits;
        this.prerequisites = currentRule.prerequisites;
        this.conflictCheck = currentRule.conflictCheck;
    }

    // Getters and Setters
    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getMaxCredits() { return maxCredits; }
    public void setMaxCredits(Integer maxCredits) { this.maxCredits = maxCredits; }

    public String getPrerequisites() { return prerequisites; }
    public void setPrerequisites(String prerequisites) { this.prerequisites = prerequisites; }

    public Boolean getConflictCheck() { return conflictCheck; }
    public void setConflictCheck(Boolean conflictCheck) { this.conflictCheck = conflictCheck; }
}