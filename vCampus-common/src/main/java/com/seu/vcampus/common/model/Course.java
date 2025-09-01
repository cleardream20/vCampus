package com.seu.vcampus.common.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Course implements Serializable {
    private String courseId;
    private String courseName;
    private Integer credit;
    private Integer capacity;
    private Integer selected = 0;
    private String teacherId;
    private String schedule;

    public List<TimeSlot> parseSchedule() {
        if (schedule == null || schedule.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<TimeSlot> slots = new ArrayList<>();
        String[] parts = schedule.split(";");

        for (String part : parts) {
            if (part.length() < 5) continue; // 确保格式正确

            TimeSlot slot = new TimeSlot();
            slot.setDay(part.substring(0, 3));

            String[] range = part.substring(3).split("-");
            if (range.length == 2) {
                try {
                    slot.setStartSlot(Integer.parseInt(range[0]));
                    slot.setEndSlot(Integer.parseInt(range[1]));
                    slots.add(slot);
                } catch (NumberFormatException e) {
                    // 忽略格式错误的时间段
                }
            }
        }
        return slots;
    }

    public static class TimeSlot implements Serializable {
        private String day;
        private int startSlot;
        private int endSlot;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public int getStartSlot() {
            return startSlot;
        }

        public void setStartSlot(int startSlot) {
            this.startSlot = startSlot;
        }

        public int getEndSlot() {
            return endSlot;
        }

        public void setEndSlot(int endSlot) {
            this.endSlot = endSlot;
        }

        @Override
        public String toString() {
            return day + startSlot + "-" + endSlot;
        }
    }

    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getSelected() {
        return selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", credit=" + credit +
                ", capacity=" + capacity +
                ", selected=" + selected +
                ", teacherId='" + teacherId + '\'' +
                ", schedule='" + schedule + '\'' +
                '}';
    }
}