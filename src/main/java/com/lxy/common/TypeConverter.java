package com.lxy.common;

import java.util.Objects;

public class TypeConverter {

    public static Object convert(Object value, String type) {
        if(Objects.isNull(value)){
            return null;
        }

        switch (type) {
            case "string": return value.toString();
            case "number": return Integer.parseInt(value.toString());
            default: return value;
        }
    }
}
