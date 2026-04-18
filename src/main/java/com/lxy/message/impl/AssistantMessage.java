package com.lxy.message.impl;

import com.lxy.message.AbstractMessage;
import lombok.Data;

import java.util.List;

@Data
public class AssistantMessage extends AbstractMessage {

    String content;

    List<ToolCall> toolCalls;

    private

    static class ToolCall{
        private Integer index;
        private String id;
        private String type;
        private FunctionCallRequest function;
    }

    static class FunctionCallRequest{
        String name;
        String arguments;
    }

}
