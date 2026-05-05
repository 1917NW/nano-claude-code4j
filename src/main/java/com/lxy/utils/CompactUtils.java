package com.lxy.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lxy.common.CurrentEnvironment;
import com.lxy.message.Message;
import com.lxy.message.impl.AssistantMessage;
import com.lxy.message.impl.ToolMessage;
import com.lxy.message.impl.UserMessage;
import com.lxy.model.ChatModel;
import com.lxy.model.NonStreamChatResponse;
import com.lxy.tools.ToolManager;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

@Slf4j
public class CompactUtils {

    private static final Long TOOL_RESULT_MAX_LENGTH = 100L;
    private static final Integer TOOL_RESULT_OFFSET = 3;

    private static final List<String> NOT_COMPACT_TOOL = new ArrayList<>();

    private static final String TRANSCRIPT_DIR = CurrentEnvironment.WORK_DIR + "/.transcripts";

    static {
        NOT_COMPACT_TOOL.add("read_file");

        File file = new File(TRANSCRIPT_DIR);
        if (!file.exists()) {
             file.mkdirs();
        }
    }

    // 如果工具调用的结果太大，则保存到文件系统并压缩
    private String compactToolResult(String toolResult){
        if(Objects.nonNull(toolResult) && toolResult.length()> TOOL_RESULT_MAX_LENGTH){
            // TODO 1.调用文件系统保存 2.压缩
            return StrUtil.EMPTY;
        }

        return toolResult;
    }

    // 把旧的ToolResult替换成占位符
    private void microCompact(List<Message> messageList){
        List<AssistantMessage> assistantToolCallMessageList = new ArrayList<>();
        Map<String, ToolMessage> toolMessageMap = new HashMap<>();
        for(Message message : messageList){
            if(message instanceof AssistantMessage){
                AssistantMessage assistantMessage = (AssistantMessage)message;
                if(CollectionUtil.isNotEmpty(assistantMessage.getToolCalls())){
                    assistantToolCallMessageList.add(assistantMessage);
                }
            }

            if(message instanceof ToolMessage){
                ToolMessage toolMessage = (ToolMessage)message;
                toolMessageMap.put(toolMessage.getToolCallId(), toolMessage);
            }
        }

        if(messageList.size() <= TOOL_RESULT_OFFSET){
            return;
        }

        List<AssistantMessage> removeToolCallMessageList = new ArrayList<>();
        int endIndex = messageList.size() - TOOL_RESULT_OFFSET;
        for(int i = 0; i < endIndex; i++){
            removeToolCallMessageList.add(assistantToolCallMessageList.get(i));
        }


        Map<String, String> removeToolCallIdMap = new HashMap<>();
        for(AssistantMessage removeToolCallMessage : removeToolCallMessageList){
            List<AssistantMessage.ToolCall> toolCalls = removeToolCallMessage.getToolCalls();
            for(AssistantMessage.ToolCall toolCall : toolCalls){
                if(NOT_COMPACT_TOOL.contains(toolCall.getId())){
                    continue;
                }

                removeToolCallIdMap.put(toolCall.getId(), toolCall.getFunction().getName());
            }
        }

        for(String removeToolCallId : removeToolCallIdMap.keySet()){
            ToolMessage toolMessage = toolMessageMap.get(removeToolCallId);
            if(Objects.nonNull(toolMessage) && toolMessage.getContent().length() > TOOL_RESULT_MAX_LENGTH){
                toolMessage.setContent(String.format("之前使用了%s工具", removeToolCallIdMap.get(removeToolCallId)));
            }
        }



    }

    // 压缩所有消息
    private List<Message> compactContext(List<Message> messageList){
        try {
            String fileName = String.format(TRANSCRIPT_DIR + "/transcript_%d", System.currentTimeMillis());
            Path path = Paths.get(fileName);
            for (Message message : messageList) {
                Files.write(
                        path,
                        JSONUtil.toJsonStr(message).getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        String content = JSONUtil.toJsonStr(messageList);

        String prompt = "总结这段对话，方便后续续接上下文，要求包含："
                + "1.已经完成了什么 2.当前处于什么状态 3.做过哪些关键决策" +content;
        NonStreamChatResponse summaryResponse = ChatModel.instance.chat(null, Collections.singletonList(new UserMessage(prompt)), ToolManager.getParentTools());
        AssistantMessage assistantMessage = summaryResponse.getAssistantMessage();

        return Collections.singletonList(new UserMessage(assistantMessage.getContent()));
    }
}
