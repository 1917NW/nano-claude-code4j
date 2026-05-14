package com.lxy.background;

import lombok.Data;

@Data
public class RuntimeTaskRecord {
    private String id;

    private String command;

    private RuntimeTaskStatusEnum status;

    private Long startedAt;

    private String resultPreview;

    private String output;
}
