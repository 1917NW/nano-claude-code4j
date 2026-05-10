package com.lxy.permisson.rules.deny.impl;

import cn.hutool.json.JSONObject;
import com.lxy.permisson.rules.deny.DenyRule;

public class BashDenyRule extends DenyRule {
    @Override
    public boolean match(String toolName, JSONObject tool_input, JSONObject context) {
        return false;
    }
}
