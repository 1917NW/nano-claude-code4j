package com.lxy.message.impl;

import com.lxy.common.RoleType;
import com.lxy.message.AbstractMessage;
import com.lxy.message.Message;
import lombok.Data;

@Data
public class UserMessage extends AbstractMessage {

    String content;

    public UserMessage(String content) {
        this.content = content;
        this.role = RoleType.User.getRole();
    }
}
