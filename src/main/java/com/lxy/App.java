package com.lxy;

import com.lxy.common.CurrentEnvironment;
import com.lxy.message.Message;
import com.lxy.message.impl.UserMessage;
import com.lxy.state.ChatState;

import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 *
 *
 */
public class App {
    public static void main( String[] args ) {
        initEnvironment();
        String systemPrompt = String.format("你是一个工作在%s目录下的编程Agent，使用工具来解决问题。", CurrentEnvironment.WORK_DIR);
        ChatState chatState = new ChatState(systemPrompt);
        while(true){
            System.out.print("(Enter exit to quit)>>>");
            Scanner scanner = new Scanner(System.in);
            String query = scanner.nextLine();
            if(query.equals("exit")){
                break;
            }
            chatState.addMessage(new UserMessage(query));
            AgentLoop.agentLoop(chatState);
            List<Message> messageList = chatState.getMessageList();
            Message message = messageList.get(messageList.size() - 1);
            if(Objects.nonNull(message)) {
                System.out.println("(answer)>>>" + message.getContent());
            }

        }
        System.out.println("---END---");
    }

    public static void initEnvironment(){
        CurrentEnvironment.setWorkDir(Paths.get("").toAbsolutePath().toString());
    }
}
