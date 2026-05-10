package com.lxy.hook;

import cn.hutool.core.util.StrUtil;
import com.lxy.hook.handler.HookHandler;

import java.util.List;

public class HookRunner {
    public static HookResult runHooks(HookEvent event) {
        String eventName = event.getName();

        List<HookHandler> hooks = HookRegister.getHooks(eventName);
        for(HookHandler hook : hooks){
            HookResult hookResult = hook.handle(event.getPayload());
            if(HookExitCodeEnum.STOP.equals(hookResult.getExitCode())
            || HookExitCodeEnum.ADD_MESSAGE.equals(hookResult.getExitCode())){
                return hookResult;
            }

        }

        return new HookResult(HookExitCodeEnum.NORMAL, StrUtil.EMPTY);
    }
}
