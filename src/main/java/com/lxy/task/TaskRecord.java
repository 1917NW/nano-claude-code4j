package com.lxy.task;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TaskRecord {
    Integer id;
    String subject;
    String description;
    TaskStatusEnum status;
    List<Integer> blockedBy = new ArrayList<>();
    List<Integer> blocks = new ArrayList<>();
    String owner;

    public boolean isReady(){
        return TaskStatusEnum.PENDING.equals(status) && CollectionUtil.isEmpty(blockedBy);
    }
}
