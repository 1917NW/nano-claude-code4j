package com.lxy.hook.handler.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.lxy.hook.HookExitCodeEnum;
import com.lxy.hook.HookResult;
import com.lxy.hook.handler.HookHandler;

public class WatchHookHandler implements HookHandler {
    @Override
    public HookResult handle(JSONObject payload) {
        System.out.println("WatchHookHandler:" + payload.toString());
        return new HookResult(HookExitCodeEnum.NORMAL, StrUtil.EMPTY);
    }
}
