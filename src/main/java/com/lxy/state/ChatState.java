package com.lxy.state;

import com.lxy.message.Message;
import com.lxy.message.impl.UserMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class ChatState {
    List<Message> messageList;
    String transitionReason;
    Integer turnCount;

    public ChatState(){
        messageList = new ArrayList<Message>();
        turnCount = 0;
    }

    public ChatState(String userQuery){
        this();
        messageList.add(new UserMessage(userQuery));
    }

    public void increaseTurnCount(){
        turnCount++;
    }
}
