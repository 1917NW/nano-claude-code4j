package com.lxy.hook;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HookEvent {
    String name;
    JSONObject payload;
}
