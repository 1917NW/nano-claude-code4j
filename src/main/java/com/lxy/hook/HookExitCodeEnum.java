package com.lxy.hook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HookExitCodeEnum {
    NORMAL(0),
    STOP(1),
    ADD_MESSAGE(2);

    Integer exitCode;
}
