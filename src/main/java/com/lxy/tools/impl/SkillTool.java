package com.lxy.tools.impl;

import com.lxy.skills.SkillRegistry;
import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;

public class SkillTool {

    @FunctionCall(name = "load_skill", description = "根据名称加载特定的专业知识")
    public String getSkillBody(@ParamProperty(description = "专业名称") String skillName) {
        return SkillRegistry.getSkillBody(skillName);
    }
}
