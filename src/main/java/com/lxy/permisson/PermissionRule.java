package com.lxy.permisson;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public abstract class PermissionRule {

    String tool;

    BehaviorEnum behavior;

    String reason;

    public abstract boolean match(String toolName, JSONObject tool_input, JSONObject context);

    public DecisionResult decision(){
        return new DecisionResult(behavior, reason);
    }
}
