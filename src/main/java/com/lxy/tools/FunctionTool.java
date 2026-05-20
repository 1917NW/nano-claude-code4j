package com.lxy.tools;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
public class FunctionTool extends Tool {

    public Function function;


    public FunctionTool(){
        this.type ="function";
    }

    public FunctionTool(Function function){
        this.function = function;
        this.type ="function";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Function{
        String name;
        String description;
        JSONObject parameters;

    }
}
