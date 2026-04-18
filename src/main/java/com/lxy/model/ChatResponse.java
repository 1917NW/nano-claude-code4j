package com.lxy.model;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private String id;
    private String object;
    private Long created;
    private String model;

    private JSONObject data;

    public ChatResponse(JSONObject result) {
        this.data = result;
    }

}
