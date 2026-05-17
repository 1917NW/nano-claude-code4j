package com.lxy.schedule;

import com.lxy.tools.annoation.ObjectProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleRecord {
    private String id;
    @ObjectProperty(description = "cron表达式")
    private String cron;

    @ObjectProperty(description = "任务prompt")
    private String prompt;
    private LocalDateTime createdAt;
    private LocalDateTime lastFiredAt;

    @ObjectProperty(description = "该任务是否为周期任务，true为周期任务，false为一次性任务")
    public boolean recurring;

}
