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

    public static String SYSTEM_PROMPT = String.format("你是一个工作在%s目录下的编程Agent，使用Todo工具来计划多步的任务，记得在开始之前标记将要执行的步骤为IN_PROGRESS，当完成后，标记该步骤为COMPLETED。尽量优先使用工具，而不是文字说明", CurrentEnvironment.WORK_DIR);

    public static void main( String[] args ) {
        initEnvironment();
        ChatState chatState = new ChatState(SYSTEM_PROMPT);
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
