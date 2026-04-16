package com.lxy.model;

import com.lxy.message.Message;
import com.lxy.tools.Tool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRequest {
    private String model;
    private List<Message> messages;
    private boolean stream;
    private List<Tool> tools;
}
