package com.lxy.tools.dto;

public enum TodoItemStatus {

    PENDING,
    IN_PROGRESS,
    COMPLETED;

    public static boolean contain(String status){
        for (TodoItemStatus itemStatus : TodoItemStatus.values()){
            if (itemStatus.name().equals(status)){
                return true;
            }
        }
        return false;
    }
}
