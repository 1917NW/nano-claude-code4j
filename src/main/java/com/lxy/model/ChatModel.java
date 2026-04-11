package com.lxy.model;

import cn.hutool.json.JSONObject;
import com.lxy.message.Message;

import java.util.List;

public class ChatModel {

    private String model;

    private String baseUrl;

    public ChatModel(String model, String baseUrl) {
        this.model = model;
        this.baseUrl = baseUrl;
    }

    public JSONObject chat(List<Message> messageList) {
        JSONObject result = new JSONObject();
        return result;
    }
}
