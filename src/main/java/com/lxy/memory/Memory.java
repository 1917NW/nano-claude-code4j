package com.lxy.memory;

import lombok.Data;

@Data
public class Memory {
    private String name;
    private String description;
    private MemoryType type;
    private String content;
}
