package com.lxy.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ReflectInvokeUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static Object invokeByJson(Object target, String methodName, String json) {
        try {
            Class<?> clazz = target.getClass();
            Method method = findMethod(clazz, methodName);
            if (method == null) {
                throw new RuntimeException("未找到方法: " + methodName);
            }

            Object[] args = buildMethodArgs(method, json);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException("反射调用失败", e);
        }
    }

    public static Object invokeByJson(Object target, Method method, String json) {
        try {
            Class<?> clazz = target.getClass();
            Object[] args = buildMethodArgs(method, json);
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException("反射调用失败", e);
        }
    }

    private static Method findMethod(Class<?> clazz, String methodName) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    private static Object[] buildMethodArgs(Method method, String json) throws Exception {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (parameterTypes.length == 0) {
            return new Object[0];
        }

        JavaType mapLikeType = OBJECT_MAPPER.getTypeFactory().constructMapType(
                java.util.Map.class, String.class, Object.class
        );
        java.util.Map<String, Object> paramMap = OBJECT_MAPPER.readValue(json, mapLikeType);

        java.lang.reflect.Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            String paramName = parameters[i].getName();
            Object rawValue = paramMap.get(paramName);

            if (rawValue == null) {
                args[i] = null;
                continue;
            }

            String rawJson = OBJECT_MAPPER.writeValueAsString(rawValue);
            args[i] = OBJECT_MAPPER.readValue(
                    rawJson,
                    OBJECT_MAPPER.getTypeFactory().constructType(genericParameterTypes[i])
            );
        }

        return args;
    }
}
