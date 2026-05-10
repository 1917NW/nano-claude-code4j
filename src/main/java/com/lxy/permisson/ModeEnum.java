package com.lxy.permisson;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ModeEnum {
    DEFAULT("default"),
    PLAN("plan"),
    AUTO("auto"),;

    private String name;

    public boolean equals(String mode) {
        for (ModeEnum modeEnum : values()) {
            if (modeEnum.getName().equals(mode)) {
                return true;
            }
        }

        return false;
    }
}
