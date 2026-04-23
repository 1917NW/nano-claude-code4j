package com.lxy.tools.dto;

import com.lxy.tools.annoation.ObjectProperty;

public class TodoItem {
    @ObjectProperty(description = "条目id")
    private String id;

    @ObjectProperty(description = "条目内容")
    private String text;

    @ObjectProperty(description = "条目状态")
    private TodoItemStatus status;
}
