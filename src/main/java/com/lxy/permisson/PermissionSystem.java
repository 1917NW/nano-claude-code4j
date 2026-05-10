package com.lxy.permisson;

import cn.hutool.json.JSONObject;
import com.lxy.permisson.rules.allow.AllowRule;
import com.lxy.permisson.rules.deny.DenyRule;
import com.lxy.permisson.rules.mode.ModeRule;
import com.lxy.permisson.rules.deny.impl.BashDenyRule;
import com.lxy.permisson.rules.mode.impl.WriteToolPlanModeRule;

import java.util.ArrayList;
import java.util.List;

public class PermissionSystem {

    static List<DenyRule> denyRules = new ArrayList<DenyRule>();

    static List<ModeRule> modeRules = new ArrayList<>();

    static List<AllowRule> allowRules = new ArrayList<>();

    static {
        denyRules.add(new BashDenyRule());

        modeRules.add(new WriteToolPlanModeRule());
    }

    public static DecisionResult checkPermission(String tool, JSONObject toolInput, JSONObject toolContext){

        for(DenyRule denyRule : denyRules){
            if(denyRule.match(tool, toolInput, toolContext)){
                return denyRule.decision();
            }
        }

        for(ModeRule modeRule : modeRules){
            if(modeRule.match(tool, toolInput, toolContext)){
                return modeRule.decision();
            }
        }

        for(AllowRule allowRule : allowRules){
            if(allowRule.match(tool, toolInput, toolContext)){
                return allowRule.decision();
            }
        }


        return new DecisionResult(BehaviorEnum.ASK, "需要用户确认并授权");
    }

}
