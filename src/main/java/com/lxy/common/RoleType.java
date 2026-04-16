package com.lxy.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleType {

    User("user"),
    System("system"),
    Assistant("assistant"),;
    String role;

}
