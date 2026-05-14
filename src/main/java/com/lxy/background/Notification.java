package com.lxy.background;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Notification {
    private String taskId;
    private RuntimeTaskStatusEnum status;
    private String command;
    private String result;
}

