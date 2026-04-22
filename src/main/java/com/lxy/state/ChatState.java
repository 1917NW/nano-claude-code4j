package com.lxy.state;

import com.lxy.message.Message;
import com.lxy.message.impl.SystemMessage;
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

    public ChatState(String systemPrompt){
        this();
        messageList.add(new SystemMessage(systemPrompt));
    }

    public void addMessage(Message message){
        messageList.add(message);
    }

    public void increaseTurnCount(){
        turnCount++;
    }
}
