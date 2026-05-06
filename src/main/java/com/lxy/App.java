package com.lxy;

import cn.hutool.json.JSONUtil;
import com.lxy.common.CurrentEnvironment;
import com.lxy.message.Message;
import com.lxy.message.impl.UserMessage;
import com.lxy.skills.SkillRegistry;
import com.lxy.state.ChatState;
import com.lxy.utils.CompactUtils;

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 *
 *
 */
public class App {


    public static void main(String[] args ) {
        ChatState chatState = new ChatState();
        while(true){
            System.out.print("(Enter exit to quit)>>>");
            Scanner scanner = new Scanner(System.in);
            String query = scanner.nextLine();
            if(query.equals("exit") || query.equals("quit") || query.equals("q")){
                break;
            }

            if(query.equals("/compact")){
                chatState.setMessageList(CompactUtils.compactContext(chatState.getMessageList()));
                System.out.println("(answer)>>>" + "压缩已完成");
                continue;
            } else if(query.equals("/message")){
                List<Message> messageList = chatState.getMessageList();
                System.out.println("(answer)>>>" + JSONUtil.toJsonStr(messageList));
                continue;
            } else{
                UserMessage userMessage = new UserMessage(query);
                chatState.addMessage(userMessage);
                chatState.setCurrentPrompt(query);
                if(CurrentEnvironment.log) {
                    System.out.printf("User:%s%n", JSONUtil.toJsonStr(userMessage));
                }
            }

            AgentLoop.agentLoop(chatState);
            List<Message> messageList = chatState.getMessageList();
            Message message = messageList.get(messageList.size() - 1);
            if(Objects.nonNull(message)) {
                System.out.println("(answer)>>>" + message.getContent());
            }

        }
        System.out.println("---END---");
    }
}
