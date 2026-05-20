package com.lxy.mcp;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lxy.tools.FunctionTool;
import com.lxy.tools.Tool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class McpToolRouter {

    // <server_name, mcp_client>
    private Map<String, McpClient> clientMap = new ConcurrentHashMap<>();

    public static McpToolRouter instance = new McpToolRouter();

    public boolean isMcpTool(String toolName){
        return toolName.startsWith("mcp_");
    }

    public void registerMcpClient(McpClient mcpClient){
        clientMap.put(mcpClient.getMcpConfig().getName(), mcpClient);
    }

    public String callMcpServer(String toolName, JSONObject args){
        String[] parts = toolName.split("_");
        String serverName = parts[1];
        String realToolName = parts[2];
        McpClient mcpClient = clientMap.get(serverName);
        return mcpClient.callTool(realToolName, args);
    }

    public List<JSONObject> getAllMcpTools(){
        List<JSONObject> tools = new ArrayList<>();
        Collection<McpClient> values = clientMap.values();
        for(McpClient mcpClient : values){
            List<FunctionTool.Function> agentTools = mcpClient.getAgentTools();
            for (FunctionTool.Function functionTool : agentTools) {
                FunctionTool functionTool1 = new FunctionTool(functionTool);
                JSONObject entries = JSONUtil.parseObj(functionTool1);
                tools.add(entries);
            }
        }

        return tools;
    }
}
