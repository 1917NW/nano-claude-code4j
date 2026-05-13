package com.lxy.tools.impl;

import com.lxy.common.CurrentEnvironment;
import com.lxy.task.TaskManager;
import com.lxy.task.TaskRecord;
import com.lxy.task.TaskStatusEnum;
import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;

import java.util.List;

public class TaskTool {

    private TaskManager taskManager;

    public TaskTool() {
        taskManager = new TaskManager(CurrentEnvironment.WORK_DIR + "/.tasks");
    }

    @FunctionCall(name = "task_create", description = "创建一个新的任务")
    public TaskRecord createTask(@ParamProperty(description = "具体任务内容") String subject,@ParamProperty(description = "任务补充内容") String description) {
        return taskManager.createTask(subject, description);
    }

    @FunctionCall(name = "task_update", description = "更新一个任务的状态或者更新一个任务的依赖")
    public TaskRecord updateTask(@ParamProperty(description = "任务id") Integer taskId, @ParamProperty(description = "任务状态") TaskStatusEnum status, @ParamProperty(description = "需要被添加的任务依赖") List<Integer> addBlockedBy, @ParamProperty(description = "需要被移除的任务依赖") List<Integer> removeBlockedBy) {
        return taskManager.updateTask(taskId, status, addBlockedBy, removeBlockedBy);
    }

    @FunctionCall(name = "task_list", description = "列出所有的任务的信息，包括该任务的状态")
    public String listTask(){
        return taskManager.listAll();
    }

    @FunctionCall(name = "task_get", description = "通过ID获取某一个任务")
    public TaskRecord getTask(@ParamProperty(description = "任务id") Integer taskId) {
        return taskManager.getTask(taskId);
    }
}
