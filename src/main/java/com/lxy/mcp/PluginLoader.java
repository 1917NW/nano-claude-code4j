package com.lxy.mcp;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PluginLoader {

    private String searchDir;
    private Map<String, JSONObject> plugins;

    public PluginLoader(String searchDir) {
        this.searchDir = searchDir;
        this.plugins = new HashMap<>();
    }

    public List<String> scan(){
        List<String> foundPlugins = new ArrayList<>();
        File dir = new File(searchDir);
        File[] files = dir.listFiles();
        for (File file : files) {
            if(file.isDirectory()){
                String fileName = file.getPath() + "/.claude-plugin/plugin.json";
                File pluginFile = new File(fileName);
                if(pluginFile.exists()){
                    String pluginName = file.getName();
                    foundPlugins.add(pluginName);
                    String content = FileUtil.readString(pluginFile, StandardCharsets.UTF_8);
                    JSONObject plugin = JSONUtil.parseObj(content);
                    plugins.put(pluginName, plugin);
                }
            }
        }

        return foundPlugins;
    }

    public Map<String, McpConfig> getMcpServer(){
        Map<String, McpConfig> mcpServerMap = new HashMap<>();
        for (Map.Entry<String, JSONObject> entry : plugins.entrySet()) {
            String pluginName = entry.getKey();
            JSONObject plugin = entry.getValue();
            JSONObject mcpServers = plugin.getJSONObject("mcpServers");
            if(Objects.isNull(mcpServers)){
                continue;
            }

            Set<String> keys = mcpServers.keySet();
            for(String key : keys){
                JSONObject jsonObject = mcpServers.getJSONObject(key);
                McpConfig mcpConfig = jsonObject.toBean(McpConfig.class);
                mcpConfig.setName(key);
                mcpServerMap.put(pluginName+ "_" + key, mcpConfig);
            }
        }

        return mcpServerMap;
    }

    public static void main(String[] args) {
        PluginLoader pluginLoader = new PluginLoader(System.getProperty("user.dir") + "/plugins");
        pluginLoader.scan();
        Map<String, McpConfig> mcpServer = pluginLoader.getMcpServer();
        System.out.println(JSONUtil.toJsonStr(mcpServer));
    }
}
