package com.lxy.schedule;

import java.time.LocalDateTime;

public class Scheduler {

    public String createSchedule(String taskDesc, String cron){
        ScheduleRecord scheduleRecord = new ScheduleRecord();
        scheduleRecord.setCron(cron);
        scheduleRecord.setCron(cron);
        scheduleRecord.setCreatedAt(LocalDateTime.now().toString());

    }
}
