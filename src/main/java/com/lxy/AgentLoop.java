package com.lxy;

import cn.hutool.core.collection.CollectionUtil;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.openai.internal.chat.Message;
import dev.langchain4j.model.openai.internal.chat.UserMessage;

import java.util.ArrayList;
import java.util.List;

public class AgentLoop {

    public static void agentLoop(String query){
        List<Message> messageList = new ArrayList<Message>();
        messageList.add(UserMessage.builder().content(query).build());
        while(true){
            AiMessage aiMessage = callLLM(messageList, new ArrayList<Tool>());
            List<ToolExecutionRequest> toolExecutionRequests = aiMessage.toolExecutionRequests();
            if(CollectionUtil.isNotEmpty(toolExecutionRequests)){

            }
        }
    }

    public static AiMessage callLLM(List<Message> messageList, List<Tool> toolList){
        return AiMessage.builder().build();
    }




}
