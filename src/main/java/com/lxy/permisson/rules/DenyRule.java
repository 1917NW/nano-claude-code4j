package com.lxy.permisson.rules;

import cn.hutool.json.JSONObject;
import com.lxy.permisson.BehaviorEnum;
import com.lxy.permisson.PermissionRule;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class DenyRule extends PermissionRule {
    String path;
    String content;

    public DenyRule(String path, String content) {
        this.setBehavior(BehaviorEnum.DENY);
        this.path = path;
        this.content = content;
    }

    public DenyRule() {
        this.setBehavior(BehaviorEnum.DENY);
    }

}
