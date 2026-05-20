package com.lxy.mcp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class McpConfig {
    String name;
    String command;
    String type;
    String url;
    List<String> args;
    Map<String, String> env;
}
