package com.lxy.permisson.rules.deny;

import com.lxy.permisson.BehaviorEnum;
import com.lxy.permisson.PermissionRule;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class DenyRule extends PermissionRule {

    public BehaviorEnum getBehavior(){
        return BehaviorEnum.DENY;
    }


}
