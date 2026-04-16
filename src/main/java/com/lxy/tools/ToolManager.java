package com.lxy.tools;

import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;
import com.lxy.tools.impl.WeatherTool;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolManager {

    static Map<String, Tool> toolMap = new HashMap<String, Tool>();

    static {
        addTool(WeatherTool.class);
    }

    private static void addTool(Class<?> ToolClazz){
        Method[] methods = ToolClazz.getMethods();
        for (Method method : methods) {
            if(method.isAnnotationPresent(FunctionCall.class)){
                FunctionCall functionCall = method.getAnnotation(FunctionCall.class);
                FunctionTool functionTool = new FunctionTool();

                FunctionTool.Function function = new FunctionTool.Function();
                function.setName(functionCall.name());
                function.setDescription(functionCall.description());

                FunctionTool.FunctionParam functionParam = new FunctionTool.FunctionParam();
                List<String> required = new ArrayList<String>();
                Map<String, FunctionTool.Property> propertyMap = new HashMap<>();
                Parameter[] parameters = method.getParameters();
                for(Parameter parameter : parameters){
                    if(parameter.isAnnotationPresent(ParamProperty.class)){
                        ParamProperty paramProperty = parameter.getAnnotation(ParamProperty.class);
                        FunctionTool.Property property = new FunctionTool.Property();
                        property.setType(paramProperty.type());
                        property.setDescription(paramProperty.description());
                        propertyMap.put(paramProperty.name(), property);

                        if(paramProperty.required()){
                            required.add(paramProperty.name());
                        }
                    }
                }

                functionParam.setProperties(propertyMap);
                functionParam.setRequired(required);
                function.setParameters(functionParam);

                functionTool.setFunction(function);
                toolMap.put(functionCall.name(), functionTool);
            }
        }

    }



    public static List<Tool> getTools() {
        List<Tool> tools = new ArrayList<>();
        for(String name : toolMap.keySet()){
            Tool tool = toolMap.get(name);
            tools.add(tool);
        }
        return tools;
    }

    public static Object executeTool(Tool tool) {
        return null;
    }
}
