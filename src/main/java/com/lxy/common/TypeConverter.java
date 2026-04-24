package com.lxy.common;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lxy.tools.dto.TodoItem;
import com.lxy.tools.impl.TodoTool;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TypeConverter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    public static Object convert(Object value, String type) {
        if(Objects.isNull(value)){
            return null;
        }

        switch (type) {
            case "string": return value.toString();
            case "integer": return Integer.parseInt(value.toString());
            default: return value;
        }
    }

    public static <T> T fromJson(String json, java.lang.reflect.Type type) {
        try {
            return OBJECT_MAPPER.readValue(
                    json,
                    OBJECT_MAPPER.getTypeFactory().constructType(type)
            );
        } catch (Exception e) {
            throw new RuntimeException("JSON 转对象失败", e);
        }
    }

    public static void main(String[] args) {
        Class<TodoTool> todoToolClass = TodoTool.class;
        Method[] methods = todoToolClass.getMethods();
        Method todoWrite = Arrays.stream(methods).filter(method -> method.getName().equals("todoTest")).findFirst().get();
        Parameter[] parameters = todoWrite.getParameters();
        Class<?> type = parameters[0].getType();
        String strings = "[{\"id\": \"1\", \"text\": \"查询北京今天的天气\", \"status\": \"IN_PROGRESS\"}, {\"id\": \"2\", \"text\": \"根据天气写一封情书\", \"status\": \"PENDING\"}, {\"id\": \"3\", \"text\": \"将情书写入testTodo.txt文件\", \"status\": \"PENDING\"}]";

        Object o = fromJson(strings, type);
        List<TodoItem> todoItemList = (List<TodoItem>) o;

        System.out.println(JSONUtil.toJsonStr(todoItemList));



    }


}
