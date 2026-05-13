package com.lxy.task;

public enum TaskStatusEnum {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    DELETED;

    public static TaskStatusEnum getTaskStatusEnum(String status) {
        for (TaskStatusEnum taskStatusEnum : TaskStatusEnum.values()) {
            if (taskStatusEnum.name().equals(status)) {
                return taskStatusEnum;
            }
        }
        return null;
    }
}
