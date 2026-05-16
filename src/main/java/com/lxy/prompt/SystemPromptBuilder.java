package com.lxy.prompt;


import cn.hutool.core.collection.CollectionUtil;
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

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SystemPromptBuilder {

    String core;

    List<ToolInfo> tools;

    List<SkillMetaInfo> skills;

    List<Memory> memories;

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

    public String build(){
        return core + "\n"
                + buildTools() + "\n"
                + buildSkills();
    }

    private String buildTools(){
        if(CollectionUtil.isEmpty(tools)){
            return "没有可用的工具";
        }

        List<String> lines = new ArrayList<>();
        for(ToolInfo toolInfo: tools){
            lines.add(String.format("  - %s: %s", toolInfo.getName(), toolInfo.getDescription()));
        }

        return "你能使用的工具列表为:\n" + String.join("\n", lines);
    }

    private String buildSkills(){
        if(CollectionUtil.isEmpty(skills)){
            return "没有可用的Skills";
        }

        List<String> lines = new ArrayList<>();
        for(SkillMetaInfo skillMetaInfo: skills){
            lines.add(String.format("  - %s: %s", skillMetaInfo.getName(), skillMetaInfo.getDescription()));
        }

        return "以下是可以使用的Skill:\n" + String.join("\n", lines);
    }

    private String buildMemories(){
        List<String> sections = new ArrayList<>();
        memoryMap.forEach((name, memory) -> {
            sections.add(String.format("## [%s]", memory.getType()));
            sections.add(String.format("### [%s]", memory.getDescription()));
            sections.add(String.format("[%s]", memory.getContent()));
        });

        return String.join("\n", sections);
    }
}
