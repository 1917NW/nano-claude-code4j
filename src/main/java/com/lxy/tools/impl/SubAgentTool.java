package com.lxy.tools.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.lxy.common.CurrentEnvironment;
import com.lxy.http.FinishReasonEnum;
import com.lxy.message.Message;
import com.lxy.message.impl.AssistantMessage;
import com.lxy.message.impl.ToolMessage;
import com.lxy.message.impl.UserMessage;
import com.lxy.model.ChatModel;
import com.lxy.http.NonStreamChatResponse;
import com.lxy.tools.ToolManager;
import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class SubAgentTool {

    public static String SUBAGENT_SYSTEM_PROMPT =
            String.format("你是一个工作在%s目录下的编程Agent，完成给定的任务，然后进行总结", CurrentEnvironment.WORK_DIR);

    @FunctionCall(name = "run_subagent", description = "启动一个拥有全新上下文的子代理，在一个干净的上下文里面执行一个子任务，然后返回一段总结")
    public String subAgent(@ParamProperty(description = "子任务的prompt") String prompt, @ParamProperty(description = "子任务的简短介绍") String description){
        log.info("subTask(prompt={}, description={}) is executing", prompt, description);

        List<Message> messages = new ArrayList<>();
        UserMessage userMessage = new UserMessage(prompt);
        if(CurrentEnvironment.log){
            System.out.printf("- SubAgnt:User:%s%n", JSONUtil.toJsonStr(userMessage));
        }
        messages.add(userMessage);


        AssistantMessage lastAssistantMessage = null;
        // 子Agent最多循环 30次，防止子Agent无限循环
        int loop_count = 30;
        for(int i = 0; i < loop_count; i++){
            NonStreamChatResponse chatResponse = ChatModel.instance.chat(SUBAGENT_SYSTEM_PROMPT, messages, ToolManager.getSubTools());
            lastAssistantMessage = chatResponse.getAssistantMessage();
            if(CurrentEnvironment.log){
                System.out.printf("- SubAgnt:Assistant:%s%n", JSONUtil.toJsonStr(lastAssistantMessage));
            }
            messages.add(lastAssistantMessage);

            String finishReason = chatResponse.finishReason();
            if(!FinishReasonEnum.TOOL_CALL.isEqual(finishReason)){
                break;
            }

            List<AssistantMessage.ToolCall> toolCalls = lastAssistantMessage.getToolCalls();
            if(CollectionUtil.isNotEmpty(toolCalls)){
                for(AssistantMessage.ToolCall toolCall : toolCalls){
                    ToolMessage toolMessage = new ToolMessage(toolCall.getId(), ToolManager.executeToolCall(toolCall));
                    if(CurrentEnvironment.log){
                        System.out.printf("- SubAgnt:Tool:%s%n", JSONUtil.toJsonStr(toolMessage));
                    }
                    messages.add(toolMessage);
                }
            }
        }

        messages.add(new UserMessage("总结该任务的结果，<reminder>不需要执行过程，只需要执行结果</reminder>"));
        NonStreamChatResponse chatResponse = ChatModel.instance.chat(SUBAGENT_SYSTEM_PROMPT, messages, ToolManager.getSubTools());
        lastAssistantMessage = chatResponse.getAssistantMessage();
        if(Objects.isNull(lastAssistantMessage)){
            return "(没有总结)";
        }

        String content = lastAssistantMessage.getContent();
        return Objects.isNull(content) ? "(没有总结)" : content;
    }
}
