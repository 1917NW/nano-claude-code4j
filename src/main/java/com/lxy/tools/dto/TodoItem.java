package com.lxy.tools.dto;

import com.lxy.tools.annoation.ObjectProperty;

public class TodoItem {
    @ObjectProperty
    private String id;

    @ObjectProperty
    private String text;

    @ObjectProperty
    private TodoItemStatus status;
}
