package com.lxy.tools;

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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Function{
        String name;
        String description;
        FunctionParam parameters;

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class FunctionParam {
        String type = "object";
        Map<String, Property> properties;
        List<String> required;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Property {
        String description;
        String type;
    }

}
