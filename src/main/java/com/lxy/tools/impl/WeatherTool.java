package com.lxy.tools.impl;


import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;


public class WeatherTool {

    @FunctionCall(name = "get_weather", description = "查询某个城市的天气")
    public String getTodayWeather(@ParamProperty(description = "城市", required = true) String city){
        return "晴转多云";
    }


}
