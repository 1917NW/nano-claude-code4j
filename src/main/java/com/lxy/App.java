package com.lxy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.lxy.agent.AgentLoop;
import com.lxy.common.CurrentEnvironment;
import com.lxy.mcp.McpClient;
import com.lxy.mcp.McpConfig;
import com.lxy.mcp.McpToolRouter;
import com.lxy.mcp.PluginLoader;
import com.lxy.message.Message;
import com.lxy.message.impl.UserMessage;
import com.lxy.schedule.CronScheduler;
import com.lxy.state.ChatState;
import com.lxy.utils.CompactUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 *
 *
 */
public class App {


    public static void main(String[] args ) {
        try {
            CurrentEnvironment.init();
            CronScheduler.instance.start();
            PluginLoader pluginLoader = new PluginLoader(System.getProperty("user.dir") + "/plugins");
            List<String> foundList = pluginLoader.scan();
            if(CollectionUtil.isNotEmpty(foundList)){
                Map<String, McpConfig> mcpServer = pluginLoader.getMcpServer();
                for (Map.Entry<String, McpConfig> entry : mcpServer.entrySet()) {
                    McpConfig mcpConfig = entry.getValue();
                    McpClient mcpClient = new McpClient(mcpConfig);
                    if(mcpClient.connect()){
                        McpToolRouter.instance.registerMcpClient(mcpClient);
                    }
                }
            }

            ChatState chatState = new ChatState();
            while (true) {
                System.out.print("(Enter exit to quit)>>>");
                Scanner scanner = new Scanner(System.in);
                String query = scanner.nextLine();
                if (query.equals("exit") || query.equals("quit") || query.equals("q")) {
                    break;
                }

                if (query.equals("/compact")) {
                    chatState.setMessageList(CompactUtils.compactContext(chatState.getMessageList()));
                    System.out.println("(answer)>>>" + "压缩已完成");
                    continue;
                } else if (query.equals("/message")) {
                    List<Message> messageList = chatState.getMessageList();
                    System.out.println("(answer)>>>" + JSONUtil.toJsonStr(messageList));
                    continue;
                } else if (query.equals("/permission")) {
                    // TODO 切换权限模式，放权给llm
                    continue;
                } else {
                    UserMessage userMessage = new UserMessage(query);
                    chatState.addMessage(userMessage);
                    chatState.setCurrentPrompt(query);
                    if (CurrentEnvironment.log) {
                        System.out.printf("User:%s%n", JSONUtil.toJsonStr(userMessage));
                    }
                }

                AgentLoop.agentLoop(chatState);
                List<Message> messageList = chatState.getMessageList();
                Message message = messageList.get(messageList.size() - 1);
                if (Objects.nonNull(message)) {
                    System.out.println("(answer)>>>" + message.getContent());
                }

            }

            System.out.println("---END---");
        } finally {
            CronScheduler.instance.stop();
        }
    }
}
