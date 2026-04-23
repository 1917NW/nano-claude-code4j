package com.lxy.tools;

import cn.hutool.json.JSONObject;
import com.lxy.common.StringCaseConverter;
import com.lxy.common.TypeConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            args[i] = param.get(StringCaseConverter.camelToSnake(parameter.getName()));
        }

        return invoke(args);
    }
    public Object invoke(Object[] args) {
        try {
            return method.invoke(target, args);
        }catch (Exception e){
           log.error(e.getMessage(), e);
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class FunctionArg{
        String name;
        String type;
    }

    public static void main(String[] args) {
        System.out.println(StringCaseConverter.camelToSnake("fileName"));
    }
}
