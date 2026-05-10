package com.lxy.model;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lxy.common.JsonKeyConverter;
import com.lxy.http.ChatRequest;
import com.lxy.http.NonStreamChatResponse;
import com.lxy.message.Message;
import com.lxy.message.impl.SystemMessage;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ChatModel {

    public static ChatModel instance;

    static {
        String model = "deepseek-chat";
        String baseUrl = "https://api.deepseek.com/chat/completions";
        String apiKey = System.getProperty("api.key");
        instance = new ChatModel(model, baseUrl, apiKey);
    }


    private String model;

    private String baseUrl;

    private String apiKey;

    public ChatModel(String model, String baseUrl, String apiKey) {
        this.model = model;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public NonStreamChatResponse chat(String systemPrompt, List<Message> messageList, List<JSONObject> toolList) {
        NonStreamChatResponse result = new NonStreamChatResponse();
        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(new CurlLoggingInterceptor())
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .callTimeout(10, TimeUnit.MINUTES)
                .build();

        List<Message> messages = new ArrayList<>();
        if(Objects.nonNull(systemPrompt)){
            messages.add(new SystemMessage(systemPrompt));
        }

        if(CollectionUtil.isNotEmpty(messageList)) {
            messages.addAll(messageList);
        }

        ChatRequest chatRequest = ChatRequest.builder()
                .model(model)
                .messages(messages)
                .tools(toolList)
                .stream(false)
                .build();
        String camelJsoStr = JSONUtil.toJsonStr(chatRequest);
        String underlineJsonStr = JsonKeyConverter.camelToUnderlineJson(camelJsoStr);
        RequestBody body = RequestBody.create(underlineJsonStr, MediaType.parse("application/json;charset=utf-8"));

        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if(Objects.nonNull(response.body())) {
                String underlineResult = response.body().string();
                String camelResult = JsonKeyConverter.underlineToCamelJson(underlineResult);
                result = JSONUtil.toBean(camelResult, NonStreamChatResponse.class);
            }}
        catch (IOException e) {
            throw new RuntimeException(e);
        }


        return result;
    }
}
