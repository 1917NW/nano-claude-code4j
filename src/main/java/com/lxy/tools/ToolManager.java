package com.lxy.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lxy.message.impl.AssistantMessage;
import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ObjectProperty;
import com.lxy.tools.annoation.ParamProperty;
import com.lxy.tools.impl.LocalFileTool;
import com.lxy.tools.impl.TodoTool;
import com.lxy.tools.impl.WeatherTool;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.*;
import java.util.*;

@Slf4j
public class ToolManager {

    // <toolName, toolDescription>
    static List<JSONObject> toolList = new ArrayList<>();

    // <toolName, toolInvoke>
    static Map<String, FunctionInvoker> functionInvokeMap = new HashMap<>();

    static {
        addTool(WeatherTool.class);
        addTool(LocalFileTool.class);
        addTool(TodoTool.class);
    }


    private static void addTool(Class<?> ToolClazz)  {
        try {
            Method[] methods = ToolClazz.getMethods();
            Object instance = ToolClazz.newInstance();
            for (Method method : methods) {
                FunctionCall functionCall = method.getAnnotation(FunctionCall.class);
                if(Objects.isNull(functionCall)){
                    continue;
                }

                String name = functionCall.name();

                JSONObject tool = parseFunctionTool(method);
                toolList.add(tool);

                functionInvokeMap.put(name, new FunctionInvoker(instance, method));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }


    public static JSONObject parseFunctionTool(Method method) {
        FunctionCall functionCall = method.getAnnotation(FunctionCall.class);
        if(Objects.isNull(functionCall)){
            throw new IllegalArgumentException("Method must be annotated with @Tool");
        }

        JSONObject root =  new JSONObject();
        root.set("type", "function");

        JSONObject function = new JSONObject();
        function.set("name", functionCall.name());
        function.set("description", functionCall.description());
        root.set("function", function);

        JSONObject parameters = new JSONObject();
        parameters.set("type", "object");
        function.set("parameters", parameters);

        JSONObject properties = new JSONObject();
        parameters.set("properties", properties);

        Parameter[] params = method.getParameters();
        Type[] genericTypes = method.getGenericParameterTypes();
        List<String> requiredParam = new ArrayList<>();
        for(int i = 0; i < params.length; i++){
            Parameter param = params[i];

            ParamProperty paramProperty = param.getAnnotation(ParamProperty.class);
            if(Objects.isNull(paramProperty)){
                continue;
            }

            String name = param.getName();
            properties.set(name, propertyJson(param.getType(), genericTypes[i], paramProperty.description()));

            if(paramProperty.required()){
                requiredParam.add(name);
            }
        }
        parameters.set("required", requiredParam);

        return root;

    }

    // TODO:1.Map参数没有解析
    private static JSONObject propertyJson(Class<?> type, Type genericType, String description){
        JSONObject properties = new JSONObject();

        if(type == String.class || type == Character.class || type == char.class){
            properties.set("type", "string");
        } else if(type == Integer.class || type == int.class){
            properties.set("type", "integer");
        } else if(type == Double.class || type == double.class || type == Float.class || type == float.class){
            properties.set("type", "double");
        } else if(type == Boolean.class || type == boolean.class){
            properties.set("type", "boolean");
        } else if(List.class.isAssignableFrom(type)){
            properties.set("type", "array");

            JSONObject items = new JSONObject();
            Type elementType = getListElementClass(genericType);
            if(Objects.isNull(elementType)){
                items.set("type", "object");
            }

            if(elementType instanceof Class<?>){
                Class<?> listElementClass = (Class<?>) elementType;
                properties.set("items", propertyJson(listElementClass, listElementClass, StrUtil.EMPTY));
            }

            if(elementType instanceof ParameterizedType){
                ParameterizedType parameterizedType = (ParameterizedType) elementType;
                Type rawType = parameterizedType.getRawType();
                properties.set("items", propertyJson((Class<?>) rawType, parameterizedType, StrUtil.EMPTY));
            }

        } else if(Map.class.isAssignableFrom(type)){
            properties.set("type", "map");
        } else {
            if(type.isEnum()){
                properties.set("type", "string");
                properties.set("enum", type.getEnumConstants());
            } else {
                properties.set("type", "object");
                JSONObject filedProperties = new JSONObject();
                Field[] fields = type.getDeclaredFields();
                List<String> requiredFieldList = new ArrayList<>();
                for (Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }

                    ObjectProperty objectProperty = field.getAnnotation(ObjectProperty.class);
                    if (Objects.isNull(objectProperty)) {
                        continue;
                    }
                    filedProperties.set(field.getName(), propertyJson(field.getType(), field.getGenericType(), objectProperty.description()));

                    if(objectProperty.required()){
                        requiredFieldList.add(field.getName());
                    }
                }
                properties.set("properties", filedProperties);
                properties.set("required", requiredFieldList);
            }
        }

        if(StrUtil.isNotBlank(description)) {
            properties.set("description", description);
        }
        return properties;
    }

    private static Type getListElementClass(Type genericType) {
        if(!(genericType instanceof ParameterizedType)){
            return null;
        }

        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

        if(actualTypeArguments.length != 1){
            return null;
        }

        return actualTypeArguments[0];

    }





    public static List<JSONObject> getTools() {
        return toolList;
    }

    public static Object executeTool(ToolExecuteRequest toolExecuteRequest) {
        String toolName = toolExecuteRequest.toolName;
        FunctionInvoker functionInvoker = functionInvokeMap.get(toolName);
        if(Objects.isNull(functionInvoker)){
            return null;
        }
        return functionInvoker.invoke(toolExecuteRequest.getFunctionParam());
    }

    public static Object executeToolCall(AssistantMessage.ToolCall toolCall) {
        ToolExecuteRequest toolExecuteRequest = new ToolExecuteRequest();
        toolExecuteRequest.setToolName(toolCall.getFunction().getName());
        toolExecuteRequest.setFunctionParam(JSONUtil.parseObj(toolCall.getFunction().getArguments()));
        return ToolManager.executeTool(toolExecuteRequest);
    }
}
