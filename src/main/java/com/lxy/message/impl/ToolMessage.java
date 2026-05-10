package com.lxy.message.impl;

import com.lxy.http.RoleType;
import com.lxy.message.AbstractMessage;
import lombok.Data;

@Data
public class ToolMessage extends AbstractMessage {
    String toolCallId;

    public ToolMessage(String toolCallId, Object content) {
        this.toolCallId = toolCallId;
        this.content = content;
        this.role = RoleType.TOOL.getRole();
    }
}
