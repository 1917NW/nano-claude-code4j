package com.lxy.message.impl;

import com.lxy.common.RoleType;
import com.lxy.message.AbstractMessage;
import com.lxy.message.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserMessage extends AbstractMessage {

    public UserMessage(String content) {
        this.content = content;
        this.role = RoleType.USER.getRole();
    }
}
