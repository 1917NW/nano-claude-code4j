package com.lxy.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleType {

    USER("user"),
    SYSTEM("system"),
    ASSISTANT("assistant"),
    TOOL("tool");
    String role;

}
