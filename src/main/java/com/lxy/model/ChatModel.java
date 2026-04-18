package com.lxy.model;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lxy.common.JsonKeyConverter;
import com.lxy.http.CurlLoggingInterceptor;
import com.lxy.message.Message;
import com.lxy.tools.Tool;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ChatModel {

    private String model;

    private String baseUrl;

    private String apiKey;

    public ChatModel(String model, String baseUrl, String apiKey) {
        this.model = model;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public JSONObject chat(List<Message> messageList, List<Tool> toolList) {
        JSONObject result = new JSONObject();
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new CurlLoggingInterceptor())
                .build();


        ChatRequest chatRequest = ChatRequest.builder()
                .model(model)
                .messages(messageList)
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
                result = JSONUtil.parseObj(camelResult);
            }}
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
