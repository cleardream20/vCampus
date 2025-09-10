package com.seu.vcampus.common.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlot implements Serializable {
        private String day;
        private int startSlot;
        private int endSlot;

        @Override
        public String toString() {
            return day + startSlot + "-" + endSlot;
        }
    }
}