package com.lxy.background;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lxy.common.CurrentEnvironment;
import com.lxy.tools.impl.BashTool;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
public class BackgroundManager {

    public static BackgroundManager instance = new BackgroundManager();

    private Map<String, RuntimeTaskRecord> tasks;
    private LinkedBlockingQueue<Notification> notifications;
    private ThreadPoolExecutor threadPoolExecutor;

    public BackgroundManager(){
        tasks = new HashMap<>();
        notifications = new LinkedBlockingQueue<Notification>();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int corePoolSize = Math.max(2, availableProcessors);
        int maximumPoolSize = Math.max(corePoolSize, availableProcessors * 2);
        threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );
    }



    public String run(String command){
        String taskId = UUID.randomUUID().toString().substring(0,8);
        RuntimeTaskRecord record = new RuntimeTaskRecord();
        record.setId(taskId);
        record.setCommand(command);
        record.setStatus(RuntimeTaskStatusEnum.RUNNING);
        tasks.put(taskId, record);
        threadPoolExecutor.execute(() -> this.execute(taskId, command));
        return taskId;
    }

    private void execute(String taskId, String command){
        String trimCommand = StrUtil.trim(command);
        String output = StrUtil.EMPTY;
        RuntimeTaskStatusEnum statusEnum = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", trimCommand);
            processBuilder.directory(new File(CurrentEnvironment.WORK_DIR));
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            StringBuilder outputBuilder = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputBuilder.append(line).append(System.lineSeparator());
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("命令执行失败，exitCode=" + exitCode + "\n" + output);
            }

            output = outputBuilder.toString();
            statusEnum = RuntimeTaskStatusEnum.COMPLETED;
        } catch (Exception e){
            log.error("runBash error", e);
            output = String.format("RunCommand Error: %s", e.getMessage());
            statusEnum = RuntimeTaskStatusEnum.ERROR;
        }
        RuntimeTaskRecord runtimeTaskRecord = tasks.get(taskId);
        runtimeTaskRecord.setStatus(statusEnum);
        runtimeTaskRecord.setOutput(output);

        notifications.add(new Notification(taskId, statusEnum, command, output));
    }

    public String check(String taskId){
        if(StrUtil.isNotBlank(taskId)){
            RuntimeTaskRecord runtimeTaskRecord = tasks.get(taskId);
            return JSONUtil.toJsonStr(runtimeTaskRecord);
        } else{
            return "Error: Unknown task";
        }
    }

    public String list(){
        Collection<RuntimeTaskRecord> values = tasks.values();
        List<String> lines = new ArrayList<>();
        for(RuntimeTaskRecord runtimeTaskRecord : values){
            lines.add(String.format("%s:%s %s", runtimeTaskRecord.getId(), runtimeTaskRecord.getCommand(), runtimeTaskRecord.getStatus()));
        }
        return String.join("\n", lines);

    }

    public List<Notification> drainNotifications(){
        List<Notification> result = new ArrayList<>();
        notifications.drainTo(result);
        return result;
    }




}
