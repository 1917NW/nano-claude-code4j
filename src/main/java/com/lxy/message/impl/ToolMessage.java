package com.lxy.message.impl;

import com.lxy.common.RoleType;
import com.lxy.message.AbstractMessage;
import com.lxy.message.Message;
import lombok.Data;

@Data
public class ToolMessage extends AbstractMessage {
    String toolCallId;
    Object content;

    public ToolMessage(String toolCallId, Object content) {
        this.toolCallId = toolCallId;
        this.content = content;
        this.role = "tool";
    }
}
