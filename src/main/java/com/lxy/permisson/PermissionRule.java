package com.lxy.permisson;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public abstract class PermissionRule {

    public abstract boolean match(String toolName, JSONObject tool_input, JSONObject context);

    public DecisionResult decision(){
        return new DecisionResult(getBehavior(), getReason());
    }

    public abstract BehaviorEnum getBehavior();

    public abstract String getReason();
}
