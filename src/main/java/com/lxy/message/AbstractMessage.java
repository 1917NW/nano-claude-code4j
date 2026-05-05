package com.lxy.message;


import cn.hutool.json.JSONUtil;
import lombok.Setter;

public class AbstractMessage implements Message{

    public String role;

    @Setter
    public Object content;

    public String getRole() {
        return role;
    }

    @Override
    public String getContent() {
        return JSONUtil.toJsonStr(content);
    }

}
