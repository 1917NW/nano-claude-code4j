package com.lxy.schedule;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.lxy.background.Notification;
import com.lxy.common.CurrentEnvironment;
import com.lxy.utils.CronUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
public class CronScheduler {

    private LinkedBlockingQueue<String> queue;

    private LinkedBlockingQueue<ScheduleRecord> tasks;


    private Thread checkThread;

    private AtomicBoolean stop;

    public static CronScheduler instance = new CronScheduler();

    public CronScheduler(){
        tasks = new LinkedBlockingQueue<>();
        queue = new LinkedBlockingQueue<>();
        stop = new AtomicBoolean(false);
        checkThread = new Thread(this::checkSchedule);
    }

    public void start(){
        checkThread.start();
    }

    public void stop(){
        stop.compareAndSet(false,true);
    }


    public String createSchedule(ScheduleRecord scheduleRecord){
        String taskId = UUID.randomUUID().toString().substring(0, 8);
        scheduleRecord.setId(taskId);
        scheduleRecord.setCreatedAt(LocalDateTime.now());

        tasks.add(scheduleRecord);
        saveDurable();

        String mode = scheduleRecord.isRecurring() ? "recurring" : "one-shot";
        return String.format("Created task %s (%s): cron = %s", taskId, mode, scheduleRecord.getCron());
    }

    public String deleteSchedule(String taskId){
        Iterator<ScheduleRecord> iterator = tasks.iterator();
        boolean remove = false;
        while (iterator.hasNext()){
            ScheduleRecord scheduleRecord = iterator.next();
            if(scheduleRecord.getId().equals(taskId)){
                iterator.remove();
                remove = true;
            }
        }

        if(remove){
            return String.format("Deleted task %s", taskId);
        }else {
            return String.format("task %s not found", taskId);
        }
    }

    public String listSchedules(){
        if(CollectionUtil.isEmpty(tasks)){
            return "No scheduled tasks";
        }

        List<String> lines = new ArrayList<>();
        for(ScheduleRecord scheduleRecord : tasks){
            String mode = scheduleRecord.isRecurring() ? "recurring" : "one-shot";

            lines.add(String.format("%s %s [%s] %s", scheduleRecord.getId(), scheduleRecord.getCron(), mode, scheduleRecord.getPrompt()));
        }

        return String.join("\n", lines);
    }

    public void saveDurable(){
        String dir = CurrentEnvironment.WORK_DIR + "/.schedule";
        File dirFile = new File(dir);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        String filePath = dir + "/tasks.json";

        String content = JSONUtil.toJsonStr(tasks);

        try {
            Path path = Paths.get(filePath);
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }



    public List<String> drainNotification(){
        List<String> result = new ArrayList<>();
        queue.drainTo(result);
        return result;
    }

    public void checkSchedule(){
        log.info("check schedule start !");
        while(!stop.get()) {
            List<String> oneShotTasks = new ArrayList<>();
            for (ScheduleRecord scheduleRecord : tasks) {
                LocalDateTime time = null;
                if (Objects.isNull(scheduleRecord.getLastFiredAt())) {
                    time = scheduleRecord.getCreatedAt();
                } else {
                    time = scheduleRecord.getLastFiredAt();
                }
                LocalDateTime nextTime = CronUtils.nextTime(scheduleRecord.getCron(), time);
                if (nextTime.isBefore(LocalDateTime.now())) {
                    queue.add(String.format("[Scheduled task %s]:%s", scheduleRecord.getId(), scheduleRecord.getPrompt()));
                    scheduleRecord.setLastFiredAt(LocalDateTime.now());
                    log.info("[Cron] Fired: {}", scheduleRecord.getId());

                    if (!scheduleRecord.isRecurring()) {
                        oneShotTasks.add(scheduleRecord.getId());
                    }
                }
            }

            if (CollectionUtil.isNotEmpty(oneShotTasks)) {
                tasks.removeIf(task -> oneShotTasks.contains(task.getId()));
                saveDurable();
            }
        }
    }
}
