package com.lxy.tools.impl;

import com.lxy.memory.Memory;
import com.lxy.memory.MemoryManager;
import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;

public class MemoryTool {

    private MemoryManager memoryManager;

    public MemoryTool(){
        memoryManager = new MemoryManager();
    }

    @FunctionCall(name = "save_memory", description = "从对话中保存一段持久化的记忆")
    public String saveMemory(@ParamProperty(description = "记忆") Memory memory){
        return memoryManager.saveMemory(memory);
    }
}
