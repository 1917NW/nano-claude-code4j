package com.lxy.common;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserAnswerEnum {
    YES("y"),
    NO("n");
    private String value;

    public static UserAnswerEnum findByValue(String value) {
        for (UserAnswerEnum e : values()) {
            if(e.getValue().equals(value.toLowerCase())){
                return e;
            }
        }
        return null;
    }
}
