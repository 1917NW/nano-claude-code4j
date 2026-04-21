package com.lxy.message;


import cn.hutool.json.JSONUtil;

public class AbstractMessage implements Message{

    public String role;

    public Object content;

    public String getRole() {
        return role;
    }

    @Override
    public String getContent() {
        return JSONUtil.toJsonStr(content);
    }
}
