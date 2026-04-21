package com.lxy.tools.impl;

import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;

public class LocalFileTool {

    @FunctionCall(name = "read_file", description = "读取某个文件")
    public String getTodayWeather(@ParamProperty(type = "string", description = "城市", required = true, name = "city") String city){
        return "晴转多云";
    }
}
