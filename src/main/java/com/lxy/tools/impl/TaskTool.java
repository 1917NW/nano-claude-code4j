package com.lxy.tools.impl;

import com.lxy.tools.annoation.FunctionCall;

public class TaskTool {

    @FunctionCall(name = "task", description = "在一个干净的上下文里面执行一个子任务，然后返回一段总结")
    public String subAgent(String prompt){
        return "summary_text";
    }
}
