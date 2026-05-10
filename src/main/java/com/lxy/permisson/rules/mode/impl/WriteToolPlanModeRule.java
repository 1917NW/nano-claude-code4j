package com.lxy.permisson.rules.mode.impl;

import cn.hutool.json.JSONObject;
import com.lxy.permisson.BehaviorEnum;
import com.lxy.permisson.ModeEnum;
import com.lxy.permisson.rules.mode.ModeRule;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class WriteToolPlanModeRule extends ModeRule {

    static List<String> writeTools = new ArrayList<String>();
    static {
        writeTools.add("write_file");
        writeTools.add("edit_file");
    }

    @Override
    public boolean doMatch(String toolName, JSONObject tool_input, JSONObject context) {
        if(writeTools.contains(toolName)){
            return true;
        }

        return false;
    }

    @Override
    public ModeEnum getModeEnum() {
        return ModeEnum.PLAN;
    }

    @Override
    public BehaviorEnum getBehavior() {
        return BehaviorEnum.DENY;
    }

    @Override
    public String getReason() {
        return "plan mode blocks writes";
    }
}
