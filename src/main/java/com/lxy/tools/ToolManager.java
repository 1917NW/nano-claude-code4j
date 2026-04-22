package com.lxy.tools;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lxy.message.impl.AssistantMessage;
import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;
import com.lxy.tools.impl.LocalFileTool;
import com.lxy.tools.impl.WeatherTool;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Slf4j
public class ToolManager {

    // <toolName, toolDescription>
    static Map<String, Tool> toolMap = new HashMap<String, Tool>();

    // <toolName, toolInvoke>
    static Map<String, FunctionInvoker> functionInvokeMap = new HashMap<>();

    static {
        addTool(WeatherTool.class);
        addTool(LocalFileTool.class);
    }


    private static void addTool(Class<?> ToolClazz)  {
        try {
            Method[] methods = ToolClazz.getMethods();
            Object instance = ToolClazz.newInstance();
            for (Method method : methods) {
                if (method.isAnnotationPresent(FunctionCall.class)) {
                    FunctionCall functionCall = method.getAnnotation(FunctionCall.class);
                    FunctionTool functionTool = new FunctionTool();

                    FunctionTool.Function function = new FunctionTool.Function();
                    function.setName(functionCall.name());
                    function.setDescription(functionCall.description());

                    FunctionTool.FunctionParam functionParam = new FunctionTool.FunctionParam();
                    List<String> required = new ArrayList<String>();
                    Map<String, FunctionTool.Property> propertyMap = new HashMap<>();
                    Parameter[] parameters = method.getParameters();
                    FunctionInvoker.FunctionArg[] functionArg = new FunctionInvoker.FunctionArg[parameters.length];
                    int i = 0;
                    for (Parameter parameter : parameters) {
                        if (parameter.isAnnotationPresent(ParamProperty.class)) {
                            ParamProperty paramProperty = parameter.getAnnotation(ParamProperty.class);
                            FunctionTool.Property property = new FunctionTool.Property();
                            property.setType(paramProperty.type());
                            property.setDescription(paramProperty.description());
                            propertyMap.put(paramProperty.name(), property);

                            if (paramProperty.required()) {
                                required.add(paramProperty.name());
                            }

                            functionArg[i++] = new FunctionInvoker.FunctionArg(paramProperty.name(), paramProperty.type());
                            ;
                        }

                    }

                    functionParam.setProperties(propertyMap);
                    functionParam.setRequired(required);
                    function.setParameters(functionParam);

                    functionTool.setFunction(function);
                    toolMap.put(functionCall.name(), functionTool);

                    FunctionInvoker functionInvoker = new FunctionInvoker(instance, method, functionArg);
                    functionInvokeMap.put(functionCall.name(), functionInvoker);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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

    public static Object executeTool(ToolExecuteRequest toolExecuteRequest) {

        String toolName = toolExecuteRequest.toolName;
        Tool tool = toolMap.get(toolName);
        if(Objects.isNull(tool)){
            return null;
        }

        switch (tool.getType()){
            case "function":
                return executeFunctionCall(toolName, toolExecuteRequest.getFunctionParam());
            default:
                return null;
        }

    }

    public static Object executeToolCall(AssistantMessage.ToolCall toolCall) {
        ToolExecuteRequest toolExecuteRequest = new ToolExecuteRequest();
        toolExecuteRequest.setToolName(toolCall.getFunction().getName());
        toolExecuteRequest.setFunctionParam(JSONUtil.parseObj(toolCall.getFunction().getArguments()));
        return ToolManager.executeTool(toolExecuteRequest);

    }

    private static Object executeFunctionCall(String functionName, JSONObject param) {
        FunctionInvoker functionInvoker = functionInvokeMap.get(functionName);
        if(Objects.isNull(functionInvoker)){
            return null;
        }

        return functionInvoker.invoke(param);
    }
}
