package com.lxy.common;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.Map;
import java.util.function.Function;

public final class JsonKeyConverter {

    private JsonKeyConverter() {
    }

    public static String camelToUnderlineJson(String json) {
        Object parsed = JSONUtil.parse(json);
        Object converted = convertKeys(parsed, JsonKeyConverter::camelToUnderline);
        return JSONUtil.toJsonStr(converted);
    }

    public static String underlineToCamelJson(String json) {
        Object parsed = JSONUtil.parse(json);
        Object converted = convertKeys(parsed, JsonKeyConverter::underlineToCamel);
        return JSONUtil.toJsonStr(converted);
    }

    public static String camelToUnderline(String key) {
        if (key == null || key.length() == 0) {
            return key;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            char current = key.charAt(i);
            if (Character.isUpperCase(current)) {
                if (shouldAppendUnderline(key, i)) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(current));
            } else {
                result.append(current);
            }
        }
        return result.toString();
    }

    public static String underlineToCamel(String key) {
        if (key == null || key.length() == 0) {
            return key;
        }

        StringBuilder result = new StringBuilder();
        boolean upperNext = false;
        for (int i = 0; i < key.length(); i++) {
            char current = key.charAt(i);
            if (current == '_') {
                upperNext = result.length() > 0;
                continue;
            }

            if (upperNext) {
                result.append(Character.toUpperCase(current));
                upperNext = false;
            } else {
                result.append(current);
            }
        }
        return result.toString();
    }

    private static Object convertKeys(Object value, Function<String, String> keyConverter) {
        if (value instanceof JSONObject) {
            JSONObject source = (JSONObject) value;
            JSONObject target = new JSONObject();
            for (Map.Entry<String, Object> entry : source.entrySet()) {
                target.set(keyConverter.apply(entry.getKey()), convertKeys(entry.getValue(), keyConverter));
            }
            return target;
        }

        if (value instanceof JSONArray) {
            JSONArray source = (JSONArray) value;
            JSONArray target = new JSONArray();
            for (Object item : source) {
                target.add(convertKeys(item, keyConverter));
            }
            return target;
        }

        return value;
    }

    private static boolean shouldAppendUnderline(String key, int index) {
        if (index == 0) {
            return false;
        }

        char previous = key.charAt(index - 1);
        if (previous == '_') {
            return false;
        }

        if (Character.isLowerCase(previous) || Character.isDigit(previous)) {
            return true;
        }

        return Character.isUpperCase(previous)
                && index + 1 < key.length()
                && Character.isLowerCase(key.charAt(index + 1));
    }
}
