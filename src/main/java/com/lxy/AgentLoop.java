package com.lxy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.lxy.enums.FinishReasonEnum;
import com.lxy.message.Message;
import com.lxy.message.impl.AssistantMessage;
import com.lxy.message.impl.ToolMessage;
import com.lxy.message.impl.UserMessage;
import com.lxy.model.ChatModel;
import com.lxy.model.NonStreamChatResponse;
import com.lxy.state.ChatState;
import com.lxy.tools.ToolExecuteRequest;
import com.lxy.tools.ToolManager;

import java.util.ArrayList;
import java.util.List;

public class AgentLoop {

    static ChatModel chatModel;

    static {
        String model = "deepseek-chat";
        String baseUrl = "https://api.deepseek.com/chat/completions";
        String apiKey = System.getProperty("api.key");
        chatModel = new ChatModel(model, baseUrl, apiKey);
    }

    public static void agentLoop(ChatState chatState){

        List<Message> messageList = chatState.getMessageList();
        while(true){
            NonStreamChatResponse chatResponse = chatModel.chat(messageList, ToolManager.getTools());
            AssistantMessage assistantMessage = chatResponse.getAssistantMessage();
            messageList.add(assistantMessage);

            String finishReason = chatResponse.finishReason();
            if(!FinishReasonEnum.TOOL_CALL.isEqual(finishReason)){
                return;
            }

            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            if(CollectionUtil.isNotEmpty(toolCalls)){
                toolCalls.forEach(toolCall -> {
                    messageList.add(new ToolMessage(toolCall.getId(), ToolManager.executeToolCall(toolCall)));
                });
            }

            chatState.increaseTurnCount();
            chatState.setTransitionReason(finishReason);
        }
    }





}
