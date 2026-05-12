package com.lxy.task;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

import java.util.List;

@Data
public class TaskRecord {
    Integer id;
    String subject;
    String description;
    TaskStatusEnum status;
    List<Integer> blockedBy;
    List<Integer> blocks;
    String owner;

    public boolean isReady(){
        return TaskStatusEnum.PENDING.equals(status) && CollectionUtil.isEmpty(blockedBy);
    }
}
