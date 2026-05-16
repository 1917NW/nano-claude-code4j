package com.lxy.common.dto;

import lombok.Data;

import java.util.Map;

@Data
public class MarkdownInfo {
    private Map<String, String> headers;
    private String content;
}
