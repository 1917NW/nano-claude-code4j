package com.lxy.schedule;

import lombok.Data;

@Data
public class ScheduleRecord {
    private String id;
    private String cron;
    private String prompt;
    private boolean recurring;
    private boolean durable;
    private Long createdAt;
    private Long lastFiredAt;
}
