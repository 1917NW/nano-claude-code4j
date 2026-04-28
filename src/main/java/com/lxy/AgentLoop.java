package com.lxy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.lxy.common.CurrentEnvironment;
import com.lxy.enums.FinishReasonEnum;
import com.lxy.message.Message;
import com.lxy.message.impl.AssistantMessage;
import com.lxy.message.impl.ToolMessage;
import com.lxy.message.impl.UserMessage;
import com.lxy.model.ChatModel;
import com.lxy.model.NonStreamChatResponse;
import com.lxy.skills.SkillRegistry;
import com.lxy.state.ChatState;
import com.lxy.tools.ToolManager;
import java.util.List;

public class AgentLoop {

    public static String SYSTEM_PROMPT = String.format("你是一个工作在%s目录下的Agent，要求如下：\n" +
            "1.你不需要执行具体的操作，只需要对用户的任务进行规划，使用run_subagent Tool委托给子Agent执行，并且使用todo_write及时新增或更新规划的状态，记得在开始之前使用Todo Tool将要执行的步骤标记为IN_PROGRESS，当完成该步骤后，使用todo_write Tool将该步骤标记为COMPLETED。\n" +
            "2.使用load_skill Tool来获取特定任务的知识，以下是可以使用的Skill：\n%s\n"+
            "3.尽量使用工具执行，而不是文字说明。", CurrentEnvironment.WORK_DIR, JSONUtil.toJsonStr(SkillRegistry.getSkillDescription()));

    public static void agentLoop(ChatState chatState){
        int rounds_since_todo = 0;
        List<Message> messageList = chatState.getMessageList();
        while(true){
            NonStreamChatResponse chatResponse = ChatModel.instance.chat(SYSTEM_PROMPT, messageList, ToolManager.getParentTools());
            AssistantMessage assistantMessage = chatResponse.getAssistantMessage();
            messageList.add(assistantMessage);

            if(CurrentEnvironment.log) {
                System.out.printf("Assistant:%s%n", JSONUtil.toJsonStr(assistantMessage));
            }
            String finishReason = chatResponse.finishReason();
            if(!FinishReasonEnum.TOOL_CALL.isEqual(finishReason)){
                return;
            }

            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            if(CollectionUtil.isNotEmpty(toolCalls)){
                for(AssistantMessage.ToolCall toolCall : toolCalls){
                    if("todo_write".equals(toolCall.getFunction().getName())){
                        rounds_since_todo = 0;
                    } else {
                        rounds_since_todo++;
                    }
                    ToolMessage toolMessage = new ToolMessage(toolCall.getId(), ToolManager.executeToolCall(toolCall));
                    if(CurrentEnvironment.log) {
                        System.out.printf("Tool:%s%n", JSONUtil.toJsonStr(toolMessage));
                    }
                    messageList.add(toolMessage);
                }
            }

            if(rounds_since_todo >= 3){
                messageList.add(new UserMessage("<reminder>记得更新你的todo计划</reminder>"));
            }

            chatState.increaseTurnCount();
            chatState.setTransitionReason(finishReason);
        }
    }





}
