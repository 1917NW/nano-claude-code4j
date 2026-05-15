package com.lxy.memory;

import cn.hutool.core.io.FileUtil;
import com.lxy.common.CurrentEnvironment;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryManager {

    private String memoryDir;

    public Map<String, Memory> memoryMap;

    public MemoryManager(){
        memoryDir = CurrentEnvironment.WORK_DIR + "/.memories";
        File file = new File(memoryDir);
        if(!file.exists()){
            file.mkdirs();
        }

        memoryMap = new HashMap<>();
    }

    public String saveMemory(Memory memory){
        String filePath = memoryDir + "/" + memory.getName();
        Path path = Paths.get(filePath);
        String formatter = "---\n"+
                "name:%s\n"+
                "description:%s\n"+
                "type:%s\n"+
                "---\n"+
                "%s";

        try {
            String content = String.format(formatter, memory.getName(), memory.getDescription(), memory.getType());
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));

            memoryMap.put(memory.getName(), memory);
            buildIndex();
            return String.format("保存%s记忆成功", memory.getName());
        } catch (IOException e) {
            return "保存记忆失败:" + e.getMessage();
        }
    }

    // 构建索引
    public void buildIndex(){
        List<String> lines = new ArrayList<>();
        lines.add("# Memory Index");
    }

    public Map<String, Memory> parseMemory(){

    }
}
