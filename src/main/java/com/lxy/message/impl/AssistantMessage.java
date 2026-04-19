package com.lxy.message.impl;

import com.lxy.message.AbstractMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class AssistantMessage extends AbstractMessage {

    private String content;
    private List<ToolCall> toolCalls;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ToolCall {
        private Integer index;
        private String id;
        private String type;
        private Function function;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Function {
        private String name;
        private String arguments;
    }

}
