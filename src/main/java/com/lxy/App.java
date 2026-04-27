package com.lxy;

import cn.hutool.json.JSONUtil;
import com.lxy.common.CurrentEnvironment;
import com.lxy.message.Message;
import com.lxy.message.impl.UserMessage;
import com.lxy.skills.SkillRegistry;
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

    public static String SYSTEM_PROMPT = String.format("你是一个工作在%s目录下的编程Agent，要求如下：\n" +
            "1.如果是多步骤任务，请使用Todo Tool进行规划，并及时更新规划的状态，记得在开始之前使用Todo Tool将要执行的步骤标记为IN_PROGRESS，当完成该步骤后，使用todo Tool将该步骤标记为COMPLETED。\n" +
            "2.使用Task Tool将规划后的子任务委托给子代理执行\n" +
            "3.使用LoadSkill Tool来获取特定任务的知识，以下是可以使用的Skill：\n%s\n"+
            "4.尽量优先使用工具，而不是文字说明。", CurrentEnvironment.WORK_DIR, JSONUtil.toJsonStr(SkillRegistry.getSkillDescription()));


    public static void main( String[] args ) {
        System.out.println(SYSTEM_PROMPT);
        ChatState chatState = new ChatState(SYSTEM_PROMPT);
        while(true){
            System.out.print("(Enter exit to quit)>>>");
            Scanner scanner = new Scanner(System.in);
            String query = scanner.nextLine();
            if(query.equals("exit") || query.equals("q")){
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
}
