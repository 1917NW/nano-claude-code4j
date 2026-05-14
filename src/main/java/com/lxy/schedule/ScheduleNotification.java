package com.lxy.schedule;

import lombok.Data;

@Data
public class ScheduleNotification {

    private String type;

    private String scheduleId;

    private String prompt;
}
