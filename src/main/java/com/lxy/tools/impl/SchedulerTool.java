package com.lxy.tools.impl;

import com.lxy.schedule.CronScheduler;
import com.lxy.schedule.ScheduleRecord;
import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;

public class SchedulerTool {

    @FunctionCall(name = "schedule_create", description = "创建一个定时任务或延时任务")
    public String createSchedule(@ParamProperty(description = "任务信息") ScheduleRecord scheduleRecord) {
        return CronScheduler.instance.createSchedule(scheduleRecord);
    }

    @FunctionCall(name = "schedule_delete", description = "删除一个定时任务或延时任务")
    public String deleteSchedule(@ParamProperty(description = "任务id") String taskId){
        return CronScheduler.instance.deleteSchedule(taskId);
    }

    @FunctionCall(name = "schedule_list", description = "展示所有定时任务")
    public String listSchedule(){
        return CronScheduler.instance.listSchedules();
    }
}
