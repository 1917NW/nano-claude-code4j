package com.lxy.permisson.rules.mode;

import cn.hutool.json.JSONObject;
import com.lxy.permisson.ModeEnum;
import com.lxy.permisson.PermissionRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

public abstract class ModeRule extends PermissionRule {


    @Override
    public boolean match(String toolName, JSONObject tool_input, JSONObject context) {
        ModeEnum modeEnum = getModeEnum();
        if(Objects.isNull(modeEnum)){
             throw new RuntimeException("模式规则没有配置模式");
        }

        String mode = (String) context.get("mode");
        return modeEnum.equals(mode) && doMatch(toolName, tool_input, context);
    }

    public abstract boolean doMatch(String toolName, JSONObject toolInput, JSONObject context);

    public abstract ModeEnum getModeEnum();
}
