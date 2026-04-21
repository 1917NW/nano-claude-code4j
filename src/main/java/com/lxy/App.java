package com.lxy;

import com.lxy.common.CurrentEnvironment;
import com.lxy.message.Message;
import com.lxy.state.ChatState;

import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 *
 *
 */
public class App {
    public static void main( String[] args ) {
        initEnvironment();
        while(true){
            System.out.print("(Enter exit to quit)>>>");
            Scanner scanner = new Scanner(System.in);
            String query = scanner.nextLine();
            if(query.equals("exit")){
                break;
            }
            ChatState chatState = new ChatState(query);
            AgentLoop.agentLoop(chatState);
            List<Message> messageList = chatState.getMessageList();
            Message message = messageList.get(messageList.size() - 1);
            System.out.println("(answer)>>>" + message.getContent());

        }
        System.out.println("---END---");
    }

    public static void initEnvironment(){
        CurrentEnvironment.setWorkDir(Paths.get("").toAbsolutePath().toString());
    }
}
