package com.lxy.permisson.rules.deny.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.lxy.permisson.BehaviorEnum;
import com.lxy.permisson.rules.deny.DenyRule;

public class BashDenyRule extends DenyRule {
    @Override
    public boolean match(String toolName, JSONObject tool_input, JSONObject context) {
        if(!"bash".equals(toolName)){
            return false;
        }

        String command = (String) tool_input.get("command");
        String trim = StrUtil.trim(command);
        if(trim.startsWith("sudo") || trim.startsWith("rm")){
            return true;
        }

        return false;
    }

    @Override
    public BehaviorEnum getBehavior() {
        return BehaviorEnum.DENY;
    }

    @Override
    public String getReason() {
        return "指令被禁止使用";
    }
}
