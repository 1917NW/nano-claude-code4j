package com.lxy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FinishReasonEnum {

    TOOL_CALL("tool_calls");
    private String reason;

    public boolean isEqual(String reason) {
        return this.reason.equals(reason);
    }
}
