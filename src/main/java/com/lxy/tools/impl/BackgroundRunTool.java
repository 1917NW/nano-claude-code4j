package com.lxy.tools.impl;

import com.lxy.background.BackgroundManager;
import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;

public class BackgroundRunTool {

    private BackgroundManager backgroundManager;

    public BackgroundRunTool(){
        backgroundManager = BackgroundManager.instance;
    }

    @FunctionCall(name = "run_background_task", description = "在后台线程执行命令，并立即返回任务id")
    public String runBackgroundTask(@ParamProperty(description = "命令") String command){
        return backgroundManager.run(command);
    }

    @FunctionCall(name = "check_background_task", description = "根据任务id查询任务执行状态")
    public String checkBackgroundTask(@ParamProperty(description = "任务id") String taskId){
        return backgroundManager.check(taskId);
    }

    @FunctionCall(name = "list_background_task", description = "查询所有任务的执行状态")
    public String listBackgroundTask(){
        return backgroundManager.list();
    }
}
