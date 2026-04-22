package com.lxy.message.impl;

import com.lxy.common.RoleType;
import com.lxy.message.AbstractMessage;
import com.lxy.message.Message;

public class SystemMessage extends AbstractMessage {

    public SystemMessage(String content) {
        this.content = content;
        this.role = RoleType.SYSTEM.getRole();
    }
}
