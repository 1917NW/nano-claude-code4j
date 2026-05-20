package com.lxy.mcp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lxy.tools.FunctionTool;
import com.lxy.tools.Tool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
public class McpClient {
    private McpConfig mcpConfig;
    private List<FunctionTool.Function> tools;

    private SubprocessChannel subprocessChannel;

    public McpClient(McpConfig mcpConfig){
        this.mcpConfig = mcpConfig;
        this.tools = new ArrayList<>();
    }


    public boolean connect(){
        try {
            List<String> commands = new ArrayList<>();
            commands.add(mcpConfig.getCommand());
            if (CollectionUtil.isNotEmpty(mcpConfig.getArgs())) {
                commands.addAll(mcpConfig.getArgs());
            }

            subprocessChannel = SubprocessChannel.start(commands, mcpConfig.getEnv());
            JsonRpcProtocol.CallMessage callMessage = new JsonRpcProtocol.CallMessage();
            callMessage.setMethod("initialize");
            JSONObject params = new JSONObject();
            params.set("protocolVersion", "2024-11-05");
            params.set("capabilities", new HashMap<>());

            callMessage.setParams(params);
            subprocessChannel.sendMessage(JSONUtil.toJsonStr(callMessage));
            String res = subprocessChannel.readLine();
            JSONObject response = JSONUtil.parseObj(res);
            if (response.containsKey("result")) {
                JsonRpcProtocol.CallMessage initialMessage = new JsonRpcProtocol.CallMessage();
                initialMessage.setMethod("notifications/initialized");
                subprocessChannel.sendMessage(JSONUtil.toJsonStr(initialMessage));
                return true;
            }

        } catch (Exception e){
            log.error(e.getMessage());
        }
        return false;

    }

    public List<FunctionTool.Function> listTools(){
        JsonRpcProtocol.CallMessage callMessage = new JsonRpcProtocol.CallMessage();
        callMessage.setMethod("tools/list");
        JSONObject params = new JSONObject();
        callMessage.setParams(params);
        subprocessChannel.sendMessage(JSONUtil.toJsonStr(callMessage));
        String content = subprocessChannel.readLine();
        JSONObject response = JSONUtil.parseObj(content);
        if(response.containsKey("result")){
            JSONArray toolArray = response.getJSONObject("result").getJSONArray("tools");
            tools = JSONUtil.toList(toolArray, FunctionTool.Function.class);
            return tools;
        }

        return tools;
    }

    public String callTool(String toolName, JSONObject args){
        JsonRpcProtocol.CallMessage callMessage = new JsonRpcProtocol.CallMessage();
        callMessage.setMethod("tools/call");
        JSONObject params = new JSONObject();
        params.set("name", toolName);
        params.set("arguments", args);
        callMessage.setParams(params);
        subprocessChannel.sendMessage(JSONUtil.toJsonStr(callMessage));
        String content = subprocessChannel.readLine();
        JSONObject response = JSONUtil.parseObj(content);
        if(response.containsKey("result")){
            JSONArray contentArray = response.getJSONObject("result").getJSONArray("content");
            List<String> list = JSONUtil.toList(contentArray, String.class);
            return String.join(",", list);

        }
        return StrUtil.EMPTY;
    }

    public List<FunctionTool.Function> getAgentTools(){
        return tools.stream().map(tool -> {
            FunctionTool.Function mcpTool = new FunctionTool.Function();
            BeanUtil.copyProperties(tool, mcpTool);
            mcpTool.setName("mcp_" + mcpConfig.getName() + "_" + tool.getName());
            return mcpTool;
        }).collect(Collectors.toList());
    }

    public void disconnect(){
        if(Objects.nonNull(subprocessChannel)){
            subprocessChannel.close();
        }
    }


}
