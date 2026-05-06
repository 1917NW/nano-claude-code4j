package com.lxy.tools;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lxy.common.JsonKeyConverter;
import com.lxy.common.StringCaseConverter;
import com.lxy.common.TypeConverter;
import com.lxy.tools.impl.TodoTool;
import com.lxy.utils.ReflectInvokeUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;

// 假定工具没有状态
@NoArgsConstructor
@Slf4j
public class FunctionInvoker {
    private Object target;
    private Method method;


    public FunctionInvoker(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public Object invoke(JSONObject param)  {
        return ReflectInvokeUtil.invokeByJson(target, method, JSONUtil.toJsonStr(param));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class FunctionArg{
        String name;
        String type;
    }

    public static void main(String[] args) {
        String json  = "{\"todo_items\":[[[{\"id\":\"A\",\"text\":false}]],[[{\"id\":\"B\",\"text\":true}]]]}";
        json = JsonKeyConverter.underlineToCamelJson(json);
        System.out.println(json);
        TodoTool tool = new TodoTool();
        Class<TodoTool> todoToolClass = TodoTool.class;
        Method[] methods = todoToolClass.getMethods();
        Method todoWrite = Arrays.stream(methods).filter(method1 -> method1.getName().equals("todoTest")).findFirst().get();
        System.out.println(new FunctionInvoker(tool, todoWrite).invoke(new JSONObject(json)));




    }
}
