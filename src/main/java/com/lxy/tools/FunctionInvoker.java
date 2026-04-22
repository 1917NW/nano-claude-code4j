package com.lxy.tools;

import cn.hutool.json.JSONObject;
import com.lxy.common.TypeConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.Method;

// 假定工具没有状态
@NoArgsConstructor
@Slf4j
public class FunctionInvoker {
    private Object target;
    private Method method;
    private FunctionArg[] argInfo;


    public FunctionInvoker(Object target, Method method, FunctionArg[] argInfo) {
        this.target = target;
        this.method = method;
        this.argInfo = argInfo;
    }

    public Object invoke(JSONObject param)  {
        Object[] args = new Object[argInfo.length];
        for (int i = 0; i < argInfo.length; i++) {
            FunctionArg functionArg = argInfo[i];
            args[i] = TypeConverter.convert(param.get(functionArg.name), functionArg.type );
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
}
