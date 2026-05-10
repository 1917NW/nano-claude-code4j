package com.lxy.hook;

import com.lxy.hook.handler.HookHandler;
import com.lxy.hook.handler.impl.WatchHookHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HookRegister {

    static Map<String, List<HookHandler>> hooks = new HashMap<String, List<HookHandler>>();

    static {
        registerHook("PreToolUse", new WatchHookHandler());
    }

    public static void registerHook(String eventName, HookHandler handler) {
        List<HookHandler> hookHandlers;
        if (hooks.containsKey(eventName)) {
            hookHandlers = hooks.get(eventName);
        }  else {
            hookHandlers = new ArrayList<>();
            hooks.put(eventName, hookHandlers);
        }

        hookHandlers.add(handler);
    }

    public static List<HookHandler> getHooks(String eventName) {
        List<HookHandler> hookHandlers;
        if (hooks.containsKey(eventName)) {
            hookHandlers = hooks.get(eventName);
        } else{
            hookHandlers = new ArrayList<>();
        }
        return hookHandlers;
    }
}
