package com.lxy.tools.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;
import com.lxy.tools.dto.TodoItem;
import com.lxy.tools.dto.TodoItemStatus;

import java.util.ArrayList;
import java.util.List;

public class TodoTool {

    List<TodoItem> todoItems;

    @FunctionCall(name = "todo_write", description = "新建或者更新todo")
    public String todoWrite(@ParamProperty(description = "Todo条目") List<TodoItem> todoItems) {
        if(CollectionUtil.isEmpty(todoItems)){
            return "新建或者更新条目失败";
        }

        if(todoItems.size() > 20){
            return "最多允许20条todo条目";
        }

        int inProgressItem = 0;
        List<TodoItem> validated = new ArrayList<>();
        for(TodoItem todoItem : todoItems){
            String id = todoItem.getId();
            String text = todoItem.getText();
            TodoItemStatus status = todoItem.getStatus();
            if(StrUtil.isBlank(text)){
                return String.format("itemId %s: text required", text);
            }

            if(TodoItemStatus.IN_PROGRESS.equals(status)){
                inProgressItem++;
            }

            validated.add(todoItem);
        }
        if(inProgressItem > 1){
            return "某一时间只能有一个任务处于IN_PROGRESS状态";
        }
        todoItems = validated;
        return StrUtil.EMPTY;
    }

    private String render(){
        if(CollectionUtil.isEmpty(todoItems)){
            return "No todos";
        }

        List<String> lines = new ArrayList<>();
        for(TodoItem todoItem : todoItems){
            switch (todoItem.getStatus()){
                case IN_PROGRESS:
                    lines.add(String.format("[>] %s: %s", todoItem.getId(), todoItem.getText()));
                    break;
                case COMPLETED:
                    lines.add(String.format("[x] %s: %s", todoItem.getId(), todoItem.getText()));
                    break;
                case PENDING:
                    lines.add(String.format("[ ] %s: %s", todoItem.getId(), todoItem.getText()));
                    break;
            }
        }

        return String.join("\n", lines);
    }
}
