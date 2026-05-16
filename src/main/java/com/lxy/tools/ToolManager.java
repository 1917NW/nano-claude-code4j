package com.lxy.tools;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lxy.common.CurrentEnvironment;
import com.lxy.common.JsonKeyConverter;
import com.lxy.common.UserAnswerEnum;
import com.lxy.hook.HookEvent;
import com.lxy.hook.HookExitCodeEnum;
import com.lxy.hook.HookResult;
import com.lxy.hook.HookRunner;
import com.lxy.message.impl.AssistantMessage;
import com.lxy.message.impl.ToolMessage;
import com.lxy.permisson.BehaviorEnum;
import com.lxy.permisson.DecisionResult;
import com.lxy.permisson.PermissionSystem;
import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ObjectProperty;
import com.lxy.tools.annoation.ParamProperty;
import com.lxy.tools.impl.*;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.*;
import java.util.*;

@Slf4j
public class ToolManager {

    // <toolName, toolDescription>
    static List<JSONObject> parentToolList = new ArrayList<>();

    static List<JSONObject> subToolList = new ArrayList<>();

    static List<ToolInfo> subToolInfoList = new ArrayList<>();

    // <toolName, toolInvoke>
    static Map<String, FunctionInvoker> functionInvokeMap = new HashMap<>();

    static {
        addTool(subToolList, WeatherTool.class);
        addTool(subToolList, LocalFileTool.class);
        addTool(subToolList, BashTool.class);

        addTool(parentToolList, TodoTool.class);
        addTool(parentToolList, SubAgentTool.class);
        addTool(parentToolList, SkillTool.class);
        addTool(parentToolList, TaskTool.class);
        addTool(parentToolList, BackgroundRunTool.class);
        addTool(parentToolList, MemoryTool.class);
    }


    private static void addTool(List<JSONObject> toolList, Class<?> ToolClazz)  {
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
            properties.set(name, propertyJson(param.getType(), genericTypes[i], paramProperty.description(), paramProperty.enums()));

            if(paramProperty.required()){
                requiredParam.add(name);
            }
        }
        parameters.set("required", requiredParam);

        return root;

    }

    // TODO:1.Map参数没有解析
    private static JSONObject propertyJson(Class<?> type, Type genericType, String description, String[] enums){
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
                properties.set("items", propertyJson(listElementClass, listElementClass, StrUtil.EMPTY, null));
            }

            if(elementType instanceof ParameterizedType){
                ParameterizedType parameterizedType = (ParameterizedType) elementType;
                Type rawType = parameterizedType.getRawType();
                properties.set("items", propertyJson((Class<?>) rawType, parameterizedType, StrUtil.EMPTY, null));
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
                    filedProperties.set(field.getName(), propertyJson(field.getType(), field.getGenericType(), objectProperty.description(), objectProperty.enums()));

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
        if(Objects.nonNull(enums) && enums.length > 0){
            properties.set("enums", enums);
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





    public static List<JSONObject> getParentTools() {
        return parentToolList;
    }

    public static List<JSONObject> getSubTools() {
        return subToolList;
    }


    public static Object executeTool(ToolExecuteRequest toolExecuteRequest) {
        try {
            String toolName = toolExecuteRequest.toolName;
            FunctionInvoker functionInvoker = functionInvokeMap.get(toolName);
            if (Objects.isNull(functionInvoker)) {
                return null;
            }
            return functionInvoker.invoke(toolExecuteRequest.getFunctionParam());
        }catch (Exception e){
            return "执行工具异常:" + e.getMessage();
        }
    }

    public static Object executeToolCall(AssistantMessage.ToolCall toolCall) {
        AssistantMessage.Function tool = toolCall.getFunction();
        JSONObject context = buildToolContext();
        DecisionResult decisionResult = PermissionSystem.checkPermission(tool.getName(), JSONUtil.parseObj(tool.getArguments()), context);
        if(BehaviorEnum.DENY.equals(decisionResult.getBehavior())){
            return String.format("权限禁止，原因是:%s", decisionResult.getReason());
        }

        if(BehaviorEnum.ASK.equals(decisionResult.getBehavior())){
            UserAnswerEnum userAnswer = askUser(tool);
            if(UserAnswerEnum.NO.equals(userAnswer)){
                return "此操作已被用户禁止";
            }
        }
        ToolExecuteRequest toolExecuteRequest = new ToolExecuteRequest();
        toolExecuteRequest.setToolName(tool.getName());
        toolExecuteRequest.setFunctionParam(JSONUtil.parseObj(JsonKeyConverter.underlineToCamelJson(tool.getArguments())));
        return ToolManager.executeTool(toolExecuteRequest);
    }

    static UserAnswerEnum askUser(AssistantMessage.Function tool){
        String toolRequest = String.format("请问您是否授权该工具的使用，工具名:%s, 工具参数:%s", tool.getName(), tool.getArguments());
        System.out.println("(Agent ASK)>>>" + toolRequest );
        while(true){
            System.out.print("(User Answer, please enter y/n)");
            Scanner scanner = new Scanner(System.in);
            String userAnswer = scanner.nextLine();
            UserAnswerEnum byValue = UserAnswerEnum.findByValue(userAnswer);
            if(byValue == null){
                continue;
            }
            return byValue;
        }

    }


    public static JSONObject buildToolContext(){
        JSONObject context = new JSONObject();
        context.set("mode", CurrentEnvironment.getProperty("permission.mode"));
        return context;
    }

    public static List<ToolInfo> getParentToolInfoList(){
        List<ToolInfo> parentToolInfoList = new ArrayList<>();
        for(JSONObject parentTool : parentToolList){
            ToolInfo toolInfo = new ToolInfo();
            toolInfo.setName((String) parentTool.getByPath("$.function.name"));
            toolInfo.setDescription((String) parentTool.getByPath("$.function.description"));
            parentToolInfoList.add(toolInfo);
        }

        return parentToolInfoList;

    }

    public static List<ToolInfo> getSubToolInfoList(){
        List<ToolInfo> subToolInfoList = new ArrayList<>();
        for(JSONObject subTool : subToolList){
            ToolInfo toolInfo = new ToolInfo();
            toolInfo.setName((String) subTool.getByPath("$.function.name"));
            toolInfo.setDescription((String) subTool.getByPath("$.function.description"));
            subToolInfoList.add(toolInfo);
        }

        return subToolInfoList;

    }

    public static void main(String[] args) {
        System.out.println(JSONUtil.toJsonStr(getSubToolInfoList()));
    }



}
