package com.lxy.memory;

import cn.hutool.core.io.FileUtil;
import com.lxy.common.CurrentEnvironment;
import com.lxy.common.dto.MarkdownInfo;
import com.lxy.utils.MarkdownParser;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
public class MemoryManager {

    private String memoryDir;

    private Map<String, Memory> memoryMap;

    private boolean loaded;

    public MemoryManager(){
        memoryDir = CurrentEnvironment.WORK_DIR + "/.memories";
        File file = new File(memoryDir);
        if(!file.exists()){
            file.mkdirs();
        }

        memoryMap = new HashMap<>();
        loaded = false;
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
            return String.format("保存%s记忆成功", memory.getName());
        } catch (IOException e) {
            return "保存记忆失败:" + e.getMessage();
        }
    }

    public Map<String, Memory> loadMemory(){
        File file = new File(memoryDir);
        String[] list = file.list();
        if(list == null){
            return memoryMap;
        }

        for(String fileName : list){
            if("MEMORY.md".equals(fileName)){
                continue;
            }
            File memoryFile = new File(fileName);
            String memoryContent = FileUtil.readString(memoryFile, StandardCharsets.UTF_8);
            MarkdownInfo parseResult = MarkdownParser.parse(memoryContent);
            Map<String, String> headers = parseResult.getHeaders();
            String content = parseResult.getContent();

            Memory memory = new Memory();
            memory.setName(headers.get("name"));
            memory.setDescription(headers.get("description"));
            memory.setContent(content);
            memory.setType(headers.get("type"));
            memoryMap.put(headers.get("name"), memory);
        }

        if(!memoryMap.isEmpty()){
            log.info("[Memory loaded: {} memories from {}]", memoryMap.size(), memoryDir);
        }
        loaded = true;
        return memoryMap;
    }

    public String getMemoryPrompt(){
        if(loaded && memoryMap.isEmpty()){
            return "";
        }

        if(!loaded){
            loadMemory();
        }

        if(memoryMap.isEmpty()){
            return "";
        }


        List<String> sections = new ArrayList<>();
        memoryMap.forEach((name, memory) -> {
            sections.add(String.format("## [%s]", memory.getType()));
            sections.add(String.format("### [%s]", memory.getDescription()));
            sections.add(String.format("[%s]", memory.getContent()));
        });

        return String.join("\n", sections);
    }

}
