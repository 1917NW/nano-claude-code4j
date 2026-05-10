package com.lxy.hook.handler;

import cn.hutool.json.JSONObject;
import com.lxy.hook.HookResult;

public interface HookHandler {
    public HookResult handle(JSONObject payload);
}
