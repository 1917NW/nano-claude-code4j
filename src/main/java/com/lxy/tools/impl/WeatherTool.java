package com.lxy.tools.impl;


import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;


public class WeatherTool {

    @FunctionCall(name = "get_weather", description = "查询某个城市的天气")
    public String getTodayWeather(@ParamProperty(type = "string", description = "城市", required = true, name = "city") String city){
        return "晴转多云";
    }


}
