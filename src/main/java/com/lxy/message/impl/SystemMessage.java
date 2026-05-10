package com.lxy.message.impl;

import com.lxy.http.RoleType;
import com.lxy.message.AbstractMessage;

public class SystemMessage extends AbstractMessage {

    public SystemMessage(String content) {
        this.content = content;
        this.role = RoleType.SYSTEM.getRole();
    }
}
