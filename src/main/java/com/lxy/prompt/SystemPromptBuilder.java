package com.lxy.prompt;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lxy.memory.Memory;
import com.lxy.skills.SkillDetail;
import com.lxy.skills.SkillMetaInfo;
import com.lxy.tools.Tool;
import com.lxy.tools.ToolInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SystemPromptBuilder {

    String core;

    List<ToolInfo> tools;

    List<SkillMetaInfo> skills;

    List<Memory> memories;

    String claudeMd;

    // Todo CLAUDE.md

    public SystemPromptBuilder core(String core){
        this.core = core;
        return this;
    }

    public SystemPromptBuilder tools(List<ToolInfo> tools){
        this.tools = tools;
        return this;
    }

    public SystemPromptBuilder skills(List<SkillMetaInfo> skills){
        this.skills = skills;
        return this;
    }

    public SystemPromptBuilder memories(List<Memory> memories){
        this.memories = memories;
        return this;
    }

    public SystemPromptBuilder claudeMd(String claudeMd){
        this.claudeMd = claudeMd;
        return this;
    }

    public String build(){
        return core + "\n"
                + buildTools() + "\n"
                + buildSkills() + "\n"
                + buildMemories() + "\n"
                + buildClaudeMd();
    }

    private String buildTools(){
        if(CollectionUtil.isEmpty(tools)){
            return "";
        }

        List<String> lines = new ArrayList<>();
        for(ToolInfo toolInfo: tools){
            lines.add(String.format("  - %s: %s", toolInfo.getName(), toolInfo.getDescription()));
        }

        return "你能使用的工具列表为:\n" + String.join("\n", lines);
    }

    private String buildSkills(){
        if(CollectionUtil.isEmpty(skills)){
            return "";
        }

        List<String> lines = new ArrayList<>();
        for(SkillMetaInfo skillMetaInfo: skills){
            lines.add(String.format("  - %s: %s", skillMetaInfo.getName(), skillMetaInfo.getDescription()));
        }

        return "以下是可以使用的Skill:\n" + String.join("\n", lines);
    }

    private String buildMemories(){
        if(CollectionUtil.isEmpty(memories)){
            return "";
        }

        Map<String, List<Memory>> memoryMap = memories.stream().collect(Collectors.groupingBy(Memory::getType));
        List<String> sections = new ArrayList<>();
        memoryMap.forEach((type, memoryList) -> {
            sections.add(String.format("## [%s]", type));
            memoryList.forEach(memory -> {
                sections.add(String.format("### [%s]", memory.getDescription()));
                sections.add(String.format("[%s]", memory.getContent()));
            });
        });

        return "以下是之前对话保存的记忆，请在回答问题时进行参考，如果与当下的信息冲突，则以当下的信息为准\n" + String.join("\n", sections);
    }

    private String buildClaudeMd(){
        if(StrUtil.isBlank(claudeMd)){
            return "";
        }
        return "该项目的指令链条:\n" + claudeMd;
    }
}
