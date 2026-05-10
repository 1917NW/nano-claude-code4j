package com.lxy.permisson.rules.allow.impl;

import cn.hutool.json.JSONObject;
import com.lxy.permisson.BehaviorEnum;
import com.lxy.permisson.rules.allow.AllowRule;

import java.util.ArrayList;
import java.util.List;

public class NoConditionPassRule extends AllowRule {

    static List<String> passToolRules = new ArrayList<String>();

    static {
        passToolRules.add("read_file");
        passToolRules.add("get_weather");
        passToolRules.add("run_subagent");
        passToolRules.add("todo_write");
    }

    @Override
    public boolean match(String toolName, JSONObject tool_input, JSONObject context) {
        if(passToolRules.contains(toolName)){
            return true;
        }

        return false;
    }



    @Override
    public String getReason() {
        return "";
    }
}
