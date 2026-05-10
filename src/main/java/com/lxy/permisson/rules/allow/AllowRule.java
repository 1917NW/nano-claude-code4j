package com.lxy.permisson.rules.allow;

import com.lxy.permisson.BehaviorEnum;
import com.lxy.permisson.PermissionRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AllowRule extends PermissionRule {

    @Override
    public BehaviorEnum getBehavior() {
        return BehaviorEnum.ALLOW;
    }
}
