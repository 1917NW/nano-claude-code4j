package com.lxy.state;

import com.lxy.message.Message;
import com.lxy.message.impl.SystemMessage;
import com.lxy.message.impl.UserMessage;
import com.lxy.recovery.RecoveryState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatState {
    List<Message> messageList;
    String currentPrompt;
    String transitionReason;
    Integer turnCount;
    RecoveryState recoveryState;

    public ChatState(){
        messageList = new ArrayList<Message>();
        turnCount = 0;
        recoveryState = new RecoveryState();
    }

    public void addMessage(Message message){
        messageList.add(message);
    }

    public void increaseTurnCount(){
        turnCount++;
    }

}
