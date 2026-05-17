package com.lxy.tools.impl;

import com.lxy.tools.annoation.FunctionCall;

import java.time.LocalDateTime;

public class LocalDateTimeTool {

    @FunctionCall(name = "get_local_date_time", description = "获取本地时间，精确到秒")
    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }
}
