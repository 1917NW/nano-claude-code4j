package com.lxy.memory;

import com.lxy.tools.annoation.ObjectProperty;
import lombok.Data;

@Data
public class Memory {
    @ObjectProperty(description = "简短的标识名称，比如(偏爱的标签，数据库)，注意使用英文")
    private String name;

    @ObjectProperty(description = "一行该记忆的简短的总结")
    private String description;

    @ObjectProperty(description = "记忆类型", enums = {"user", "feedback", "project", "reference"})
    private String type;

    @ObjectProperty(description = "完整的记忆内容(可以多行)")
    private String content;
}
