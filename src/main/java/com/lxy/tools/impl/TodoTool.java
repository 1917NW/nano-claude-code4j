package com.lxy.tools.impl;

import cn.hutool.core.util.StrUtil;
import com.lxy.tools.annoation.FunctionCall;
import com.lxy.tools.annoation.ParamProperty;
import com.lxy.tools.dto.TodoItem;

import java.util.List;

public class TodoTool {

    @FunctionCall(name = "todo_write", description = "新建或者更新todo")
    public String todoWrite(@ParamProperty(description = "Todo条目", required = true) List<TodoItem> items) {
        return StrUtil.EMPTY;
    }
}
