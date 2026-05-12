package com.lxy.http;

import cn.hutool.json.JSONObject;
import com.lxy.message.Message;
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
    private List<JSONObject> tools;
    private Integer maxTokens;
}
