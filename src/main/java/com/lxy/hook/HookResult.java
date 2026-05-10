package com.lxy.hook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HookResult {
    private HookExitCodeEnum exitCode;
    private String message;
}
