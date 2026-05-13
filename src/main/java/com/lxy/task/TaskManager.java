package com.lxy.task;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class TaskManager {

    String taskDir;

    public TaskManager(String taskDir) {
        this.taskDir = taskDir;
        File file = new File(taskDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public int maxId(){
        File dir = new File(taskDir);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return 0;
        }

        int maxId = 0;
        for (File file : files) {
            String fileName = file.getName();
            String[] fileNameList = fileName.split("_");
            String idText;
            if(fileNameList.length == 2){
                idText = fileNameList[1];
            } else {
                continue;
            }
            try {
                maxId = Math.max(maxId, Integer.parseInt(idText));
            } catch (NumberFormatException ignored) {
                // Ignore files whose names are not numeric ids.
            }
        }
        return maxId;
    }

    public void saveTask(TaskRecord taskRecord){
        if(Objects.isNull(taskRecord)){
            return;
        }
        String fileName = taskDir + "/task_" + taskRecord.getId() + ".json";
        Path path = Paths.get(fileName);
        String content = JSONUtil.toJsonStr(taskRecord);
        try {
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public TaskRecord createTask(String subject, String description){
        TaskRecord taskRecord = new TaskRecord();
        int taskId = maxId();
        taskRecord.setId(taskId);
        taskRecord.setSubject(subject);
        taskRecord.setDescription(description);
        taskRecord.setStatus(TaskStatusEnum.PENDING);
        taskRecord.setBlockedBy(new ArrayList<>());
        taskRecord.setOwner(StrUtil.EMPTY);

        saveTask(taskRecord);
        return taskRecord;
    }

    public TaskRecord loadTask(Integer id){
        File file = new File(taskDir + "/task_" + id + ".json");
        if (!file.exists()) {
            return null;
        }

        String content = FileUtil.readString(file, StandardCharsets.UTF_8);
        return JSONUtil.toBean(content, TaskRecord.class);
    }

    public TaskRecord updateTask(Integer taskId, TaskStatusEnum status, List<Integer> addBlockedBy, List<Integer> removeBlockedBy){
        TaskRecord taskRecord = loadTask(taskId);
        if(Objects.nonNull(status)){
            taskRecord.setStatus(status);
            if(status == TaskStatusEnum.COMPLETED){
                clearDependency(taskId);
            }
        }

        if(CollectionUtil.isNotEmpty(addBlockedBy)){
            taskRecord.getBlockedBy().addAll(addBlockedBy);
        }

        if(CollectionUtil.isNotEmpty(removeBlockedBy)){
            taskRecord.getBlockedBy().removeAll(removeBlockedBy);
        }

        saveTask(taskRecord);
        return taskRecord;
    }

    public void clearDependency(int completedId){
        File dir = new File(taskDir);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            String content = FileUtil.readString(file, StandardCharsets.UTF_8);
            TaskRecord taskRecord = JSONUtil.toBean(content, TaskRecord.class);
            if(taskRecord.blockedBy.contains(completedId)){
                taskRecord.blockedBy.remove(completedId);
                saveTask(taskRecord);
            }
        }
    }

    public String listAll(){
        File dir = new File(taskDir);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return StrUtil.EMPTY;
        }

        List<String> list = new ArrayList<>();
        for (File file : files) {
            String content = FileUtil.readString(file, StandardCharsets.UTF_8);
            TaskRecord taskRecord = JSONUtil.toBean(content, TaskRecord.class);

            String maker = "?";
            switch (taskRecord.getStatus()){
                case PENDING:
                    maker = "[ ]";
                    break;
                case IN_PROGRESS:
                    maker = "[>]";
                    break;
                case COMPLETED:
                    maker = "[x]";
                    break;
            }

            String blockedBy = "blocked by:" + JSONUtil.toJsonStr(taskRecord.getBlockedBy());

            list.add(String.format("%s # %d:%s %s", maker, taskRecord.getId(), taskRecord.getSubject(), blockedBy));

        }

        return String.join("\n", list);

    }

    public TaskRecord getTask(Integer id){
        return loadTask(id);
    }

}
